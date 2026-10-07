/**
 * Navbar & Header Component Manager
 */
document.addEventListener('DOMContentLoaded', () => {
    initHeader();
    initSearch();
    updateCartCountBadge();
});

function initHeader() {
    const user = Storage.getUser();
    const authContainer = document.getElementById('header-auth-container');
    if (!authContainer) return;

    if (user) {
        const isAdmin = user.role === 'ROLE_ADMIN';
        authContainer.innerHTML = `
            <div class="user-menu-dropdown">
                <div class="nav-item-link" style="display:flex;align-items:center;gap:6px;">
                    <span style="background:rgba(255,255,255,0.2);padding:4px 8px;border-radius:12px;font-size:13px;">👤 ${user.name.split(' ')[0]}</span>
                    <span>▾</span>
                </div>
                <div class="dropdown-content">
                    <div style="padding:10px 18px;font-weight:700;border-bottom:1px solid #eee;color:#2874f0;">
                        Hello, ${user.name}
                    </div>
                    <a href="profile.html" class="dropdown-item">👤 My Profile</a>
                    <a href="orders.html" class="dropdown-item">📦 My Orders</a>
                    ${isAdmin ? '<a href="admin.html" class="dropdown-item" style="color:#d32f2f;font-weight:700;">⚙️ Admin Dashboard</a>' : ''}
                    <div class="dropdown-divider"></div>
                    <a href="javascript:void(0)" onclick="handleLogout()" class="dropdown-item" style="color:#d32f2f;">🚪 Logout</a>
                </div>
            </div>
        `;
    } else {
        authContainer.innerHTML = `
            <a href="login.html" class="nav-link-btn">Login</a>
        `;
    }
}

function handleLogout() {
    Storage.clearAuth();
    showToast('Logged out successfully', 'info');
    setTimeout(() => {
        window.location.href = 'login.html';
    }, 500);
}

// Live search preview
let searchTimeout = null;
function initSearch() {
    const searchInput = document.getElementById('global-search-input');
    const suggestionsBox = document.getElementById('search-suggestions');
    const searchForm = document.getElementById('global-search-form');

    if (!searchInput) return;

    if (searchForm) {
        searchForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const query = searchInput.value.trim();
            if (query) {
                window.location.href = `products.html?keyword=${encodeURIComponent(query)}`;
            }
        });
    }

    searchInput.addEventListener('input', (e) => {
        const query = e.target.value.trim();
        clearTimeout(searchTimeout);

        if (query.length < 2) {
            if (suggestionsBox) suggestionsBox.style.display = 'none';
            return;
        }

        searchTimeout = setTimeout(async () => {
            try {
                const res = await apiRequest(`/products?keyword=${encodeURIComponent(query)}&size=5`);
                const products = res.products || [];
                if (suggestionsBox) {
                    if (products.length === 0) {
                        suggestionsBox.innerHTML = `<div style="padding:12px;color:#878787;font-size:13px;text-align:center;">No toys found matching "${query}"</div>`;
                    } else {
                        suggestionsBox.innerHTML = products.map(p => `
                            <div class="suggestion-item" onclick="window.location.href='product-detail.html?id=${p.id}'">
                                <img src="${p.imageUrl}" alt="${p.name}">
                                <div class="suggestion-info">
                                    <div class="suggestion-title">${p.name}</div>
                                    <div class="suggestion-price">₹${p.price.toLocaleString('en-IN')}</div>
                                </div>
                            </div>
                        `).join('') + `
                            <div style="padding:8px;text-align:center;background:#f5f7fa;border-top:1px solid #eee;">
                                <a href="products.html?keyword=${encodeURIComponent(query)}" style="color:#2874f0;font-size:12px;font-weight:600;">View all results &rarr;</a>
                            </div>
                        `;
                    }
                    suggestionsBox.style.display = 'block';
                }
            } catch (err) {
                // Ignore search error
            }
        }, 300);
    });

    // Close suggestions on outside click
    document.addEventListener('click', (e) => {
        if (suggestionsBox && !suggestionsBox.contains(e.target) && e.target !== searchInput) {
            suggestionsBox.style.display = 'none';
        }
    });
}
