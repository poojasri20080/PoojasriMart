/**
 * Admin Dashboard & Catalog Management Logic
 */
let adminProductsList = [];
let adminCategoriesList = [];
let editingProductId = null;

document.addEventListener('DOMContentLoaded', () => {
    checkAdminAuth();
    loadDashboardStats();
    loadAdminCategories();
    loadAdminProducts();
    loadAdminOrders();
    loadAdminUsers();
});

function checkAdminAuth() {
    const user = Storage.getUser();
    if (!user || user.role !== 'ROLE_ADMIN') {
        alert('Access denied. Administrator privileges required.');
        window.location.href = 'login.html?redirect=admin.html';
    }
}

// Switch Admin Tabs
function showAdminTab(tabId) {
    document.querySelectorAll('.admin-tab-content').forEach(c => c.style.display = 'none');
    document.querySelectorAll('.admin-nav-item').forEach(n => n.classList.remove('active'));

    const activeTab = document.getElementById(`tab-${tabId}`);
    if (activeTab) activeTab.style.display = 'block';

    const activeBtn = document.getElementById(`btn-tab-${tabId}`);
    if (activeBtn) activeBtn.classList.add('active');
}

// 1. Dashboard Stats
async function loadDashboardStats() {
    try {
        const stats = await apiRequest('/admin/stats');
        document.getElementById('stat-revenue').textContent = `₹${(stats.totalRevenue || 0).toLocaleString('en-IN')}`;
        document.getElementById('stat-orders').textContent = stats.totalOrders || 0;
        document.getElementById('stat-products').textContent = stats.totalProducts || 0;
        document.getElementById('stat-users').textContent = stats.totalUsers || 0;
        document.getElementById('stat-pending').textContent = stats.pendingOrders || 0;
        document.getElementById('stat-lowstock').textContent = stats.lowStockProducts || 0;
    } catch (e) {
        console.error('Stats error:', e);
    }
}

// Load Categories for dropdowns
async function loadAdminCategories() {
    try {
        adminCategoriesList = await apiRequest('/categories');
        const select = document.getElementById('product-form-category');
        if (select) {
            select.innerHTML = '<option value="">Select Category</option>' + 
                adminCategoriesList.map(c => `<option value="${c.id}">${c.name}</option>`).join('');
        }
    } catch (e) {
        console.error(e);
    }
}

// 2. Manage Products
async function loadAdminProducts() {
    const tableBody = document.getElementById('admin-products-table-body');
    if (!tableBody) return;

    try {
        adminProductsList = await apiRequest('/admin/products');
        renderAdminProductsTable(adminProductsList);
    } catch (e) {
        tableBody.innerHTML = `<tr><td colspan="7" style="text-align:center;color:#d32f2f;">Failed to load products</td></tr>`;
    }
}

function renderAdminProductsTable(products) {
    const tableBody = document.getElementById('admin-products-table-body');
    if (!tableBody) return;

    if (products.length === 0) {
        tableBody.innerHTML = `<tr><td colspan="7" style="text-align:center;padding:30px;">No products found</td></tr>`;
        return;
    }

    tableBody.innerHTML = products.map(p => `
        <tr>
            <td>
                <img src="${p.imageUrl}" alt="${p.name}" style="width:45px;height:45px;object-fit:cover;border-radius:4px;">
            </td>
            <td>
                <strong>${p.name}</strong>
                <div style="font-size:12px;color:#878787;">Brand: ${p.brand || 'PoojaMart'}</div>
            </td>
            <td>${p.category ? p.category.name : 'Uncategorized'}</td>
            <td><strong>₹${p.price.toLocaleString('en-IN')}</strong></td>
            <td>
                <span class="stock-badge ${p.stockQuantity <= 5 ? 'low-stock' : 'in-stock'}">
                    ${p.stockQuantity} in stock
                </span>
            </td>
            <td>★ ${p.rating || '4.5'}</td>
            <td>
                <button class="btn btn-sm btn-outline" onclick="openEditProductModal(${p.id})">Edit</button>
                <button class="btn btn-sm btn-danger" onclick="deleteProduct(${p.id})">Delete</button>
            </td>
        </tr>
    `).join('');
}

function searchAdminProducts() {
    const q = document.getElementById('admin-product-search').value.toLowerCase().trim();
    const filtered = adminProductsList.filter(p => 
        p.name.toLowerCase().includes(q) || 
        (p.category && p.category.name.toLowerCase().includes(q))
    );
    renderAdminProductsTable(filtered);
}

// Add/Edit Product Modal Handlers
function openAddProductModal() {
    editingProductId = null;
    document.getElementById('product-modal-title').textContent = 'Add New Toy Product';
    document.getElementById('product-crud-form').reset();
    document.getElementById('product-modal').style.display = 'flex';
}

function openEditProductModal(productId) {
    const p = adminProductsList.find(it => it.id === productId);
    if (!p) return;

    editingProductId = productId;
    document.getElementById('product-modal-title').textContent = 'Edit Toy Product';

    document.getElementById('p-name').value = p.name || '';
    document.getElementById('p-price').value = p.price || '';
    document.getElementById('p-orig-price').value = p.originalPrice || '';
    document.getElementById('p-stock').value = p.stockQuantity || '';
    document.getElementById('product-form-category').value = p.category ? p.category.id : '';
    document.getElementById('p-img').value = p.imageUrl || '';
    document.getElementById('p-brand').value = p.brand || '';
    document.getElementById('p-age').value = p.ageGroup || '';
    document.getElementById('p-desc').value = p.description || '';
    document.getElementById('p-specs').value = p.specifications || '';
    document.getElementById('p-featured').checked = !!p.featured;
    document.getElementById('p-bestseller').checked = !!p.bestSeller;
    document.getElementById('p-newarrival').checked = !!p.newArrival;

    document.getElementById('product-modal').style.display = 'flex';
}

function closeProductModal() {
    document.getElementById('product-modal').style.display = 'none';
}

async function handleSaveProduct(e) {
    e.preventDefault();
    const catId = document.getElementById('product-form-category').value;
    const category = adminCategoriesList.find(c => c.id == catId);

    const payload = {
        name: document.getElementById('p-name').value.trim(),
        price: parseFloat(document.getElementById('p-price').value),
        originalPrice: parseFloat(document.getElementById('p-orig-price').value) || null,
        stockQuantity: parseInt(document.getElementById('p-stock').value),
        category: category || null,
        imageUrl: document.getElementById('p-img').value.trim(),
        brand: document.getElementById('p-brand').value.trim(),
        ageGroup: document.getElementById('p-age').value.trim(),
        description: document.getElementById('p-desc').value.trim(),
        specifications: document.getElementById('p-specs').value.trim(),
        featured: document.getElementById('p-featured').checked,
        bestSeller: document.getElementById('p-bestseller').checked,
        newArrival: document.getElementById('p-newarrival').checked
    };

    try {
        if (editingProductId) {
            await apiRequest(`/admin/products/${editingProductId}`, 'PUT', payload);
            showToast('Toy product updated successfully!', 'success');
        } else {
            await apiRequest('/admin/products', 'POST', payload);
            showToast('New toy product created successfully!', 'success');
        }
        closeProductModal();
        loadAdminProducts();
        loadDashboardStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

async function deleteProduct(productId) {
    if (!confirm('Are you sure you want to delete this product?')) return;
    try {
        await apiRequest(`/admin/products/${productId}`, 'DELETE');
        showToast('Product deleted', 'info');
        loadAdminProducts();
        loadDashboardStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// 3. Manage Orders
async function loadAdminOrders() {
    const tableBody = document.getElementById('admin-orders-table-body');
    if (!tableBody) return;

    try {
        const orders = await apiRequest('/admin/orders');
        tableBody.innerHTML = orders.map(o => `
            <tr>
                <td><strong>${o.orderNumber}</strong></td>
                <td>
                    <div>${o.customerName}</div>
                    <div style="font-size:12px;color:#878787;">${o.customerEmail}</div>
                </td>
                <td>₹${o.payableAmount.toLocaleString('en-IN')}</td>
                <td>
                    <span style="font-size:12px;padding:3px 8px;border-radius:3px;background:#e3f2fd;color:#1565c0;font-weight:600;">
                        ${o.paymentMethod}
                    </span>
                </td>
                <td>
                    <select class="status-select" onchange="changeOrderStatus(${o.id}, this.value)">
                        <option value="PLACED" ${o.orderStatus === 'PLACED' ? 'selected' : ''}>PLACED</option>
                        <option value="CONFIRMED" ${o.orderStatus === 'CONFIRMED' ? 'selected' : ''}>CONFIRMED</option>
                        <option value="SHIPPED" ${o.orderStatus === 'SHIPPED' ? 'selected' : ''}>SHIPPED</option>
                        <option value="OUT_FOR_DELIVERY" ${o.orderStatus === 'OUT_FOR_DELIVERY' ? 'selected' : ''}>OUT FOR DELIVERY</option>
                        <option value="DELIVERED" ${o.orderStatus === 'DELIVERED' ? 'selected' : ''}>DELIVERED</option>
                        <option value="CANCELLED" ${o.orderStatus === 'CANCELLED' ? 'selected' : ''}>CANCELLED</option>
                    </select>
                </td>
                <td style="font-size:12px;color:#878787;">
                    ${new Date(o.orderDate).toLocaleDateString('en-IN')}
                </td>
            </tr>
        `).join('');
    } catch (e) {
        tableBody.innerHTML = `<tr><td colspan="6" style="text-align:center;">Failed to load orders</td></tr>`;
    }
}

async function changeOrderStatus(orderId, newStatus) {
    try {
        await apiRequest(`/admin/orders/${orderId}/status`, 'PUT', { status: newStatus });
        showToast(`Order status updated to ${newStatus}`, 'success');
        loadDashboardStats();
    } catch (err) {
        showToast(err.message, 'error');
    }
}

// 4. Manage Users
async function loadAdminUsers() {
    const tableBody = document.getElementById('admin-users-table-body');
    if (!tableBody) return;

    try {
        const users = await apiRequest('/admin/users');
        tableBody.innerHTML = users.map(u => `
            <tr>
                <td>#${u.id}</td>
                <td><strong>${u.name}</strong></td>
                <td>${u.email}</td>
                <td>${u.phone || 'N/A'}</td>
                <td>${u.city ? `${u.city}, ${u.state}` : 'N/A'}</td>
                <td>
                    <span style="font-size:12px;font-weight:700;color:${u.role === 'ROLE_ADMIN' ? '#d32f2f' : '#2874f0'};">
                        ${u.role}
                    </span>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        console.error(e);
    }
}
