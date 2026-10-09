/**
 * PoojaMart Toys - Unified API Client & State Management
 */
// Automatically route API requests to Spring Boot backend (port 8080) if accessed via Live Server or file://
const API_BASE = (window.location.port && window.location.port !== '8080') || window.location.protocol === 'file:'
    ? 'http://localhost:8080/api'
    : '/api';

const Storage = {
    getUser: () => {
        try {
            return JSON.parse(localStorage.getItem('pmt_user'));
        } catch (e) {
            return null;
        }
    },
    setUser: (user) => {
        if (user) {
            localStorage.setItem('pmt_user', JSON.stringify(user));
        } else {
            localStorage.removeItem('pmt_user');
        }
    },
    getToken: () => localStorage.getItem('pmt_token'),
    setToken: (token) => {
        if (token) {
            localStorage.setItem('pmt_token', token);
        } else {
            localStorage.removeItem('pmt_token');
        }
    },
    clearAuth: () => {
        localStorage.removeItem('pmt_user');
        localStorage.removeItem('pmt_token');
    },
    // Local cart backup for guest/fallback mode
    getLocalCart: () => {
        try {
            return JSON.parse(localStorage.getItem('pmt_cart')) || [];
        } catch (e) {
            return [];
        }
    },
    setLocalCart: (cart) => {
        localStorage.setItem('pmt_cart', JSON.stringify(cart));
    }
};

// Toast notification helper
function showToast(message, type = 'success') {
    let container = document.getElementById('toast-container');
    if (!container) {
        container = document.createElement('div');
        container.id = 'toast-container';
        document.body.appendChild(container);
    }

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.innerHTML = `
        <span>${message}</span>
        <button style="background:transparent;border:none;color:#fff;cursor:pointer;font-size:16px;margin-left:12px;" onclick="this.parentElement.remove()">&times;</button>
    `;

    container.appendChild(toast);
    setTimeout(() => {
        if (toast.parentElement) toast.remove();
    }, 3500);
}

// Fetch wrapper with error handling
async function apiRequest(endpoint, method = 'GET', data = null) {
    const headers = {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    };

    const token = Storage.getToken();
    if (token) {
        headers['Authorization'] = `Bearer ${token}`;
    }

    const options = {
        method,
        headers
    };

    if (data && (method === 'POST' || method === 'PUT' || method === 'PATCH')) {
        options.body = JSON.stringify(data);
    }

    try {
        const response = await fetch(`${API_BASE}${endpoint}`, options);
        if (!response.ok) {
            let errorMsg = `Server error (${response.status})`;
            try {
                const errData = await response.json();
                if (errData.message) errorMsg = errData.message;
            } catch (e) {}
            throw new Error(errorMsg);
        }
        return await response.json();
    } catch (err) {
        console.warn(`API Error [${endpoint}]:`, err.message);
        throw err;
    }
}

// Cart Badging sync
async function updateCartCountBadge() {
    const badges = document.querySelectorAll('.cart-badge');
    if (!badges.length) return;

    const user = Storage.getUser();
    let count = 0;

    if (user && user.id) {
        try {
            const summary = await apiRequest(`/cart/${user.id}`);
            count = summary.totalQuantity || (summary.items ? summary.items.length : 0);
        } catch (e) {
            const localCart = Storage.getLocalCart();
            count = localCart.reduce((acc, it) => acc + (it.quantity || 1), 0);
        }
    } else {
        const localCart = Storage.getLocalCart();
        count = localCart.reduce((acc, it) => acc + (it.quantity || 1), 0);
    }

    badges.forEach(b => {
        b.textContent = count;
        b.style.display = count > 0 ? 'inline-block' : 'none';
    });
}
