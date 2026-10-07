/**
 * Flipkart-style 4-Step Checkout Logic
 */
let checkoutUser = null;
let checkoutCartItems = [];
let checkoutSummary = null;

document.addEventListener('DOMContentLoaded', () => {
    initCheckout();
});

async function initCheckout() {
    checkoutUser = Storage.getUser();
    if (!checkoutUser) {
        window.location.href = 'login.html?redirect=checkout.html';
        return;
    }

    // Populate user info in Step 1
    const userDisplay = document.getElementById('checkout-user-info');
    if (userDisplay) {
        userDisplay.innerHTML = `
            <div><strong>${checkoutUser.name}</strong> (${checkoutUser.email})</div>
            <div style="font-size:13px;color:#878787;">Mobile: ${checkoutUser.phone || 'Not added'}</div>
        `;
    }

    // Populate address form in Step 2 with user saved address
    const nameInput = document.getElementById('addr-name');
    const phoneInput = document.getElementById('addr-phone');
    const streetInput = document.getElementById('addr-street');
    const cityInput = document.getElementById('addr-city');
    const stateInput = document.getElementById('addr-state');
    const pinInput = document.getElementById('addr-pin');

    if (nameInput) nameInput.value = checkoutUser.name || '';
    if (phoneInput) phoneInput.value = checkoutUser.phone || '';
    if (streetInput) streetInput.value = checkoutUser.address || '';
    if (cityInput) cityInput.value = checkoutUser.city || 'Chennai';
    if (stateInput) stateInput.value = checkoutUser.state || 'Tamil Nadu';
    if (pinInput) pinInput.value = checkoutUser.postalCode || '600040';

    // Load cart items for Step 3 and Price Summary
    try {
        checkoutSummary = await apiRequest(`/cart/${checkoutUser.id}`);
        checkoutCartItems = checkoutSummary.items || [];

        if (checkoutCartItems.length === 0) {
            alert('Your cart is empty! Redirecting to shop.');
            window.location.href = 'products.html';
            return;
        }

        renderCheckoutItems(checkoutCartItems);
        renderCheckoutPriceDetails(checkoutSummary);
    } catch (err) {
        console.error('Checkout cart load error:', err);
    }
}

function renderCheckoutItems(items) {
    const container = document.getElementById('checkout-items-list');
    if (!container) return;

    container.innerHTML = items.map(item => `
        <div style="display:flex;gap:14px;padding:12px 0;border-bottom:1px solid #f0f0f0;align-items:center;">
            <img src="${item.product.imageUrl}" alt="${item.product.name}" style="width:60px;height:60px;object-fit:cover;border-radius:4px;">
            <div style="flex:1;">
                <div style="font-size:14px;font-weight:600;">${item.product.name}</div>
                <div style="font-size:12px;color:#878787;">Qty: ${item.quantity} | ₹${item.product.price.toLocaleString('en-IN')} each</div>
            </div>
            <div style="font-size:15px;font-weight:700;">
                ₹${(item.product.price * item.quantity).toLocaleString('en-IN')}
            </div>
        </div>
    `).join('');
}

function renderCheckoutPriceDetails(summary) {
    document.getElementById('checkout-count').textContent = summary.totalQuantity || summary.itemCount;
    document.getElementById('checkout-subtotal').textContent = `₹${(summary.originalTotal || summary.subtotal).toLocaleString('en-IN')}`;
    document.getElementById('checkout-discount').textContent = `- ₹${(summary.discount || 0).toLocaleString('en-IN')}`;
    document.getElementById('checkout-delivery-fee').textContent = summary.deliveryFee > 0 ? `₹${summary.deliveryFee}` : 'FREE';
    document.getElementById('checkout-final-amount').textContent = `₹${(summary.finalAmount || summary.subtotal).toLocaleString('en-IN')}`;
}

// Payment method tab selection
function selectPaymentMethod(method) {
    document.querySelectorAll('.payment-option-card').forEach(c => c.classList.remove('selected'));
    const target = document.getElementById(`pay-card-${method}`);
    if (target) target.classList.add('selected');

    const radio = document.querySelector(`input[name="paymentMode"][value="${method}"]`);
    if (radio) radio.checked = true;

    // Show/hide sub-sections
    document.getElementById('upi-details-box').style.display = (method === 'UPI') ? 'block' : 'none';
    document.getElementById('card-details-box').style.display = (method === 'CARD') ? 'block' : 'none';
}

// Place Order
async function handlePlaceOrder() {
    const name = document.getElementById('addr-name').value.trim();
    const phone = document.getElementById('addr-phone').value.trim();
    const street = document.getElementById('addr-street').value.trim();
    const city = document.getElementById('addr-city').value.trim();
    const state = document.getElementById('addr-state').value.trim();
    const pin = document.getElementById('addr-pin').value.trim();

    if (!name || !phone || !street || !pin) {
        showToast('Please fill all delivery address details', 'error');
        return;
    }

    const paymentMethod = document.querySelector('input[name="paymentMode"]:checked').value;
    const btn = document.getElementById('btn-confirm-order');
    if (btn) {
        btn.disabled = true;
        btn.innerHTML = 'Placing Order... ⏳';
    }

    const orderPayload = {
        userId: checkoutUser.id,
        customerName: name,
        customerEmail: checkoutUser.email,
        phone: phone,
        shippingAddress: street,
        city: city,
        state: state,
        postalCode: pin,
        paymentMethod: paymentMethod,
        deliveryCharge: checkoutSummary.deliveryFee || 0,
        discountAmount: checkoutSummary.discount || 0,
        items: checkoutCartItems.map(it => ({
            productId: it.product.id,
            quantity: it.quantity
        }))
    };

    try {
        const res = await apiRequest('/orders', 'POST', orderPayload);
        if (res.success && res.order) {
            updateCartCountBadge();
            // Redirect to success page
            window.location.href = `order-success.html?orderNumber=${res.order.orderNumber}`;
        } else {
            throw new Error(res.message || 'Could not place order');
        }
    } catch (err) {
        showToast(err.message || 'Failed to place order', 'error');
        if (btn) {
            btn.disabled = false;
            btn.innerHTML = 'CONFIRM & PLACE ORDER ➔';
        }
    }
}
