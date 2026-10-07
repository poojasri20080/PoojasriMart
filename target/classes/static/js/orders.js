/**
 * Order History & Tracking Logic
 */
document.addEventListener('DOMContentLoaded', () => {
    loadUserOrders();
});

async function loadUserOrders() {
    const user = Storage.getUser();
    if (!user) {
        window.location.href = 'login.html?redirect=orders.html';
        return;
    }

    const container = document.getElementById('orders-list-container');
    const emptyBox = document.getElementById('empty-orders-view');
    if (!container) return;

    try {
        const orders = await apiRequest(`/orders/user/${user.id}`);

        if (!orders || orders.length === 0) {
            container.innerHTML = '';
            if (emptyBox) emptyBox.style.display = 'block';
            return;
        }

        if (emptyBox) emptyBox.style.display = 'none';
        container.innerHTML = orders.map(order => renderOrderCard(order)).join('');
    } catch (err) {
        container.innerHTML = `<div style="padding:40px;color:#d32f2f;text-align:center;">Failed to load order history.</div>`;
    }
}

function renderOrderCard(order) {
    const steps = ['PLACED', 'CONFIRMED', 'SHIPPED', 'OUT_FOR_DELIVERY', 'DELIVERED'];
    const currentStatus = (order.orderStatus || 'PLACED').toUpperCase();
    const isCancelled = currentStatus === 'CANCELLED';
    const currentIndex = steps.indexOf(currentStatus);

    const formattedDate = new Date(order.orderDate).toLocaleDateString('en-IN', {
        day: 'numeric', month: 'short', year: 'numeric', hour: '2-digit', minute: '2-digit'
    });

    let stepperHtml = '';
    if (isCancelled) {
        stepperHtml = `
            <div style="padding:10px 16px;background:#ffebee;color:#c62828;font-weight:700;border-radius:4px;margin:16px 0;display:inline-block;">
                ✕ This order has been CANCELLED
            </div>
        `;
    } else {
        const progressPercent = Math.max(0, Math.min(100, (currentIndex / (steps.length - 1)) * 100));
        stepperHtml = `
            <div class="order-tracking-stepper">
                <div class="stepper-line">
                    <div class="stepper-progress" style="width: ${progressPercent}%;"></div>
                </div>
                ${steps.map((step, idx) => {
                    const isDone = idx <= currentIndex;
                    const isActive = idx === currentIndex;
                    const labels = {
                        'PLACED': 'Order Placed',
                        'CONFIRMED': 'Confirmed',
                        'SHIPPED': 'Shipped',
                        'OUT_FOR_DELIVERY': 'Out for Delivery',
                        'DELIVERED': 'Delivered'
                    };
                    return `
                        <div class="stepper-step ${isDone ? 'completed' : ''} ${isActive ? 'active' : ''}">
                            <div class="stepper-circle">${isDone ? '✓' : (idx + 1)}</div>
                            <div class="stepper-label">${labels[step]}</div>
                        </div>
                    `;
                }).join('')}
            </div>
        `;
    }

    return `
        <div class="order-card-box" style="background:#fff;border-radius:4px;box-shadow:0 1px 4px rgba(0,0,0,0.08);margin-bottom:20px;border:1px solid #e0e0e0;overflow:hidden;">
            <div style="background:#f8f9fa;padding:12px 20px;display:flex;justify-content:space-between;align-items:center;border-bottom:1px solid #e0e0e0;flex-wrap:wrap;gap:10px;">
                <div>
                    <span style="font-size:12px;color:#878787;">ORDER ID: </span>
                    <strong style="color:#2874f0;font-size:14px;">${order.orderNumber}</strong>
                    <span style="margin:0 8px;color:#ccc;">|</span>
                    <span style="font-size:12px;color:#878787;">Placed on ${formattedDate}</span>
                </div>
                <div>
                    <span style="font-size:14px;font-weight:700;">Total: ₹${order.payableAmount.toLocaleString('en-IN')}</span>
                    <span style="font-size:12px;color:#878787;margin-left:6px;">(${order.paymentMethod})</span>
                </div>
            </div>

            <div style="padding:20px;">
                ${stepperHtml}

                <div style="margin-top:20px;">
                    <h5 style="font-size:13px;color:#878787;text-transform:uppercase;margin-bottom:10px;">Items in this order:</h5>
                    ${order.items.map(item => `
                        <div style="display:flex;gap:14px;padding:8px 0;border-bottom:1px solid #f5f5f5;align-items:center;">
                            <img src="${item.productImage || ''}" alt="${item.productName}" style="width:50px;height:50px;object-fit:cover;border-radius:4px;">
                            <div style="flex:1;">
                                <div style="font-weight:600;font-size:14px;">${item.productName}</div>
                                <div style="font-size:12px;color:#878787;">Qty: ${item.quantity} × ₹${item.price.toLocaleString('en-IN')}</div>
                            </div>
                            <div style="font-weight:700;font-size:14px;">
                                ₹${item.subtotal.toLocaleString('en-IN')}
                            </div>
                        </div>
                    `).join('')}
                </div>

                <div style="margin-top:16px;display:flex;justify-content:space-between;align-items:center;flex-wrap:wrap;gap:10px;">
                    <div style="font-size:12px;color:#555;">
                        <strong>Delivery Address:</strong> ${order.customerName}, ${order.shippingAddress}, ${order.city}, ${order.state} - ${order.postalCode} | Phone: ${order.phone}
                    </div>
                    ${(!isCancelled && currentStatus !== 'DELIVERED') ? `
                        <button class="btn btn-outline btn-sm" onclick="cancelOrder(${order.id})" style="color:#d32f2f;border-color:#d32f2f;">
                            Cancel Order
                        </button>
                    ` : ''}
                </div>
            </div>
        </div>
    `;
}

async function cancelOrder(orderId) {
    if (!confirm('Are you sure you want to cancel this order?')) return;
    try {
        await apiRequest(`/orders/${orderId}/cancel`, 'POST');
        showToast('Order has been cancelled.', 'info');
        loadUserOrders();
    } catch (err) {
        showToast(err.message, 'error');
    }
}
