/**
 * Product Details Page Logic
 */
let currentProduct = null;

document.addEventListener('DOMContentLoaded', () => {
    const params = new URLSearchParams(window.location.search);
    const productId = params.get('id');
    if (!productId) {
        window.location.href = 'products.html';
        return;
    }
    loadProductDetail(productId);
});

async function loadProductDetail(id) {
    try {
        currentProduct = await apiRequest(`/products/${id}`);
        renderProductView(currentProduct);
        loadRelatedProducts(id);
    } catch (err) {
        document.getElementById('product-detail-container').innerHTML = `
            <div style="padding: 60px; text-align: center;">
                <h2>Product Not Found</h2>
                <p style="color: #878787; margin: 12px 0 20px;">The toy you are looking for might have been moved or removed.</p>
                <a href="products.html" class="btn btn-primary">Browse All Toys</a>
            </div>
        `;
    }
}

function renderProductView(p) {
    document.title = `${p.name} - PoojaMart Toys`;

    // Breadcrumbs
    const bcCategory = document.getElementById('bc-category');
    const bcTitle = document.getElementById('bc-title');
    if (bcCategory && p.category) {
        bcCategory.textContent = p.category.name;
        bcCategory.href = `products.html?categoryId=${p.category.id}`;
    }
    if (bcTitle) bcTitle.textContent = p.name;

    // Main Image & Thumbnails
    const mainImg = document.getElementById('main-product-img');
    const thumbContainer = document.getElementById('product-thumbnails');
    if (mainImg) mainImg.src = p.imageUrl;

    if (thumbContainer) {
        const images = [p.imageUrl];
        if (p.additionalImages) {
            p.additionalImages.split(',').map(s => s.trim()).filter(Boolean).forEach(url => {
                if (!images.includes(url)) images.push(url);
            });
        }
        thumbContainer.innerHTML = images.map((img, idx) => `
            <div class="thumb-box ${idx === 0 ? 'active' : ''}" onclick="switchMainImage('${img}', this)">
                <img src="${img}" alt="Thumbnail">
            </div>
        `).join('');
    }

    // Info
    document.getElementById('detail-brand').textContent = p.brand || 'PoojaMart Toys';
    document.getElementById('detail-title').textContent = p.name;
    document.getElementById('detail-rating').textContent = `${p.rating || '4.5'} ★`;
    document.getElementById('detail-reviews').textContent = `${p.reviewCount || 120} Ratings & Reviews`;
    document.getElementById('detail-price').textContent = `₹${p.price.toLocaleString('en-IN')}`;

    if (p.originalPrice) {
        document.getElementById('detail-original-price').textContent = `₹${p.originalPrice.toLocaleString('en-IN')}`;
        const disc = p.discountPercent || Math.round(((p.originalPrice - p.price) / p.originalPrice) * 100);
        document.getElementById('detail-discount').textContent = `${disc}% off`;
    }

    // Stock status
    const stockBadge = document.getElementById('stock-status-badge');
    const btnAddToCart = document.getElementById('btn-add-to-cart');
    const btnBuyNow = document.getElementById('btn-buy-now');

    if (p.stockQuantity > 0) {
        stockBadge.innerHTML = `<span style="color: #388e3c; font-weight: 700;">● In Stock (${p.stockQuantity} left)</span>`;
    } else {
        stockBadge.innerHTML = `<span style="color: #d32f2f; font-weight: 700;">● Currently Out of Stock</span>`;
        if (btnAddToCart) btnAddToCart.disabled = true;
        if (btnBuyNow) btnBuyNow.disabled = true;
    }

    // Description & Specs
    document.getElementById('detail-description').textContent = p.description || '';

    const specsTable = document.getElementById('specifications-table');
    if (specsTable && p.specifications) {
        const rows = p.specifications.split('|').map(s => s.trim()).filter(Boolean);
        specsTable.innerHTML = rows.map(r => {
            const parts = r.split(':');
            const key = parts[0] ? parts[0].trim() : '';
            const val = parts[1] ? parts[1].trim() : '';
            return `
                <tr>
                    <td class="spec-label">${key}</td>
                    <td class="spec-value">${val}</td>
                </tr>
            `;
        }).join('');
    }
}

function switchMainImage(src, thumbElem) {
    const mainImg = document.getElementById('main-product-img');
    if (mainImg) mainImg.src = src;

    document.querySelectorAll('.thumb-box').forEach(t => t.classList.remove('active'));
    if (thumbElem) thumbElem.classList.add('active');
}

// Add To Cart from Detail Page
async function handleDetailAddToCart() {
    if (!currentProduct) return;
    const user = Storage.getUser();

    if (!user) {
        const localCart = Storage.getLocalCart();
        const existing = localCart.find(it => it.productId === currentProduct.id);
        if (existing) {
            existing.quantity += 1;
        } else {
            localCart.push({ productId: currentProduct.id, quantity: 1 });
        }
        Storage.setLocalCart(localCart);
        updateCartCountBadge();
        showToast('Added to Cart! (Guest mode)', 'success');
        return;
    }

    try {
        await apiRequest('/cart/add', 'POST', {
            userId: user.id,
            productId: currentProduct.id,
            quantity: 1
        });
        showToast('Added to your PoojaMart Cart!', 'success');
        updateCartCountBadge();
    } catch (err) {
        showToast(err.message || 'Failed to add item', 'error');
    }
}

// Buy Now (Instant checkout)
async function handleBuyNow() {
    if (!currentProduct) return;
    await handleDetailAddToCart();
    window.location.href = 'cart.html';
}

// Load Related Products
async function loadRelatedProducts(id) {
    try {
        const related = await apiRequest(`/products/${id}/related`);
        const container = document.getElementById('related-products-grid');
        if (container && related && related.length > 0) {
            container.innerHTML = related.map(p => createProductCardHtml(p)).join('');
        }
    } catch (err) {
        // Ignore related products errors
    }
}

// Delivery Pincode Checker
function checkPincode() {
    const pin = document.getElementById('pincode-input').value.trim();
    const result = document.getElementById('pincode-result');
    if (!/^\d{6}$/.test(pin)) {
        result.innerHTML = `<span style="color:#d32f2f;">Please enter a valid 6-digit Indian PIN code</span>`;
        return;
    }

    result.innerHTML = `
        <span style="color:#388e3c; font-weight: 600;">
            ✓ Delivery available to <b>${pin}</b> by tomorrow! | Free delivery available.
        </span>
    `;
}
