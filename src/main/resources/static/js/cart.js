/**
 * Shopping Cart Management Logic
 */
document.addEventListener('DOMContentLoaded', () => {
    loadCart();
});

async function loadCart() {
    const user = Storage.getUser();
    const container = document.getElementById('cart-items-container');
    const emptyState = document.getElementById('empty-cart-state');
    const mainLayout = document.getElementById('cart-main-layout');

    if (!user) {
        // Fallback for guest local cart: load product details
        const localItems = Storage.getLocalCart();
        if (!localItems.length) {
            if (mainLayout) mainLayout.style.display = 'none';
            if (emptyState) emptyState.style.display = 'block';
            return;
        }

        renderGuestCart(localItems);
        return;
    }

    try {
        const summary = await apiRequest(`/cart/${user.id}`);
        const items = summary.items || [];

        if (items.length === 0) {
            if (mainLayout) mainLayout.style.display = 'none';
            if (emptyState) emptyState.style.display = 'block';
            return;
        }

        if (mainLayout) mainLayout.style.display = 'grid';
        if (emptyState) emptyState.style.display = 'none';

        renderCartItems(items);
        renderPriceDetails(summary);
    } catch (err) {
        console.error('Failed to load cart:', err);
    }
}

function renderCartItems(items) {
    const container = document.getElementById('cart-items-container');
    if (!container) return;

    container.innerHTML = items.map(item => {
        const p = item.product;
        const disc = p.discountPercent || (p.originalPrice ? Math.round(((p.originalPrice - p.price)/p.originalPrice)*100) : 0);
        return `
            <div class="cart-item-row" id="cart-item-${item.id}">
                <div class="cart-item-left">
                    <img src="${p.imageUrl}" alt="${p.name}" class="cart-item-img" onclick="window.location.href='product-detail.html?id=${p.id}'">
                    <div class="qty-control-group">
                        <button class="qty-btn" onclick="changeQuantity(${item.id}, ${item.quantity - 1})">-</button>
                        <span class="qty-value">${item.quantity}</span>
                        <button class="qty-btn" onclick="changeQuantity(${item.id}, ${item.quantity + 1})">+</button>
                    </div>
                </div>
                <div class="cart-item-details">
                    <h4 class="cart-item-title" onclick="window.location.href='product-detail.html?id=${p.id}'">${p.name}</h4>
                    <div class="cart-item-seller">Seller: PoojaMart Assured</div>
                    <div class="cart-item-prices">
                        <span class="cart-item-price">₹${(p.price * item.quantity).toLocaleString('en-IN')}</span>
                        ${p.originalPrice ? `<span class="original-price">₹${(p.originalPrice * item.quantity).toLocaleString('en-IN')}</span>` : ''}
                        ${disc > 0 ? `<span class="discount-tag">${disc}% off</span>` : ''}
                    </div>
                    <div class="cart-item-actions">
                        <button class="action-link-btn" onclick="removeItem(${item.id})">REMOVE</button>
                    </div>
                </div>
                <div class="cart-item-delivery">
                    Delivery in 2 - 3 Days | <span style="color:#388e3c;font-weight:600;">FREE</span>
                </div>
            </div>
        `;
    }).join('');
}

function renderPriceDetails(summary) {
    document.getElementById('summary-count').textContent = summary.totalQuantity || summary.itemCount;
    document.getElementById('summary-original-price').textContent = `₹${(summary.originalTotal || summary.subtotal).toLocaleString('en-IN')}`;
    document.getElementById('summary-discount').textContent = `- ₹${(summary.discount || 0).toLocaleString('en-IN')}`;
    document.getElementById('summary-delivery-fee').textContent = summary.deliveryFee > 0 ? `₹${summary.deliveryFee}` : 'FREE';
    document.getElementById('summary-total-amount').textContent = `₹${(summary.finalAmount || summary.subtotal).toLocaleString('en-IN')}`;

    const savings = summary.discount || 0;
    const savingsEl = document.getElementById('summary-savings-banner');
    if (savingsEl) {
        if (savings > 0) {
            savingsEl.style.display = 'block';
            savingsEl.textContent = `You will save ₹${savings.toLocaleString('en-IN')} on this order`;
        } else {
            savingsEl.style.display = 'none';
        }
    }
}

async function changeQuantity(cartItemId, newQty) {
    try {
        if (newQty <= 0) {
            await removeItem(cartItemId);
            return;
        }

        await apiRequest('/cart/update', 'PUT', {
            cartItemId: cartItemId,
            quantity: newQty
        });
        loadCart();
        updateCartCountBadge();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function removeItem(cartItemId) {
    if (!confirm('Are you sure you want to remove this toy from your cart?')) return;
    try {
        await apiRequest(`/cart/${cartItemId}`, 'DELETE');
        showToast('Item removed from cart');
        loadCart();
        updateCartCountBadge();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// Fallback guest cart handler
async function renderGuestCart(localItems) {
    const mainLayout = document.getElementById('cart-main-layout');
    if (mainLayout) mainLayout.style.display = 'grid';

    try {
        // Fetch products for all local items
        const promises = localItems.map(it => apiRequest(`/products/${it.productId}`).catch(() => null));
        const products = await Promise.all(promises);

        const validItems = [];
        let subtotal = 0;
        let origTotal = 0;
        let totalQty = 0;

        products.forEach((p, idx) => {
            if (p) {
                const qty = localItems[idx].quantity || 1;
                validItems.push({ id: idx, product: p, quantity: qty });
                subtotal += p.price * qty;
                origTotal += (p.originalPrice || p.price) * qty;
                totalQty += qty;
            }
        });

        renderCartItems(validItems);
        renderPriceDetails({
            totalQuantity: totalQty,
            itemCount: validItems.length,
            originalTotal: origTotal,
            subtotal: subtotal,
            discount: Math.max(0, origTotal - subtotal),
            deliveryFee: subtotal > 499 ? 0 : 40,
            finalAmount: subtotal + (subtotal > 499 ? 0 : 40)
        });
    } catch (e) {
        console.error(e);
    }
}

function proceedToCheckout() {
    const user = Storage.getUser();
    if (!user) {
        showToast('Please log in to continue checkout', 'info');
        setTimeout(() => {
            window.location.href = 'login.html?redirect=checkout.html';
        }, 800);
        return;
    }
    window.location.href = 'checkout.html';
}
