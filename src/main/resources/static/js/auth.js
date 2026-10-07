/**
 * Authentication and Profile Management Logic
 */
document.addEventListener('DOMContentLoaded', () => {
    initAuthForms();
    initProfilePage();
});

function initAuthForms() {
    // Login Form
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('login-email').value.trim();
            const password = document.getElementById('login-password').value;
            const errorBox = document.getElementById('login-error-msg');
            const submitBtn = document.getElementById('login-submit-btn');

            if (errorBox) errorBox.style.display = 'none';
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.textContent = 'Logging in... ⏳';
            }

            try {
                const res = await apiRequest('/auth/login', 'POST', { email, password });
                if (res.success && res.user) {
                    Storage.setUser(res.user);
                    Storage.setToken(res.token);
                    showToast(`Welcome back, ${res.user.name}!`, 'success');

                    // Check redirect param
                    const params = new URLSearchParams(window.location.search);
                    const redirect = params.get('redirect') || (res.user.role === 'ROLE_ADMIN' ? 'admin.html' : 'index.html');
                    setTimeout(() => {
                        window.location.href = redirect;
                    }, 600);
                } else {
                    throw new Error(res.message || 'Invalid credentials');
                }
            } catch (err) {
                if (errorBox) {
                    errorBox.textContent = err.message;
                    errorBox.style.display = 'block';
                }
                showToast(err.message, 'error');
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.textContent = 'Login';
                }
            }
        });
    }

    // Register Form
    const registerForm = document.getElementById('register-form');
    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const name = document.getElementById('reg-name').value.trim();
            const email = document.getElementById('reg-email').value.trim();
            const password = document.getElementById('reg-password').value;
            const phone = document.getElementById('reg-phone').value.trim();
            const address = document.getElementById('reg-address').value.trim();
            const city = document.getElementById('reg-city').value.trim();
            const state = document.getElementById('reg-state').value.trim();
            const postalCode = document.getElementById('reg-pin').value.trim();

            const errorBox = document.getElementById('reg-error-msg');
            if (errorBox) errorBox.style.display = 'none';

            try {
                const res = await apiRequest('/auth/register', 'POST', {
                    name, email, password, phone, address, city, state, postalCode
                });
                if (res.success && res.user) {
                    Storage.setUser(res.user);
                    Storage.setToken(res.token);
                    showToast('Account created successfully! Welcome to PoojaMart Toys.', 'success');
                    setTimeout(() => {
                        window.location.href = 'index.html';
                    }, 800);
                } else {
                    throw new Error(res.message || 'Registration failed');
                }
            } catch (err) {
                if (errorBox) {
                    errorBox.textContent = err.message;
                    errorBox.style.display = 'block';
                }
                showToast(err.message, 'error');
            }
        });
    }
}

// Quick fill credentials for demo convenience
function fillDemoUser() {
    const email = document.getElementById('login-email');
    const pass = document.getElementById('login-password');
    if (email && pass) {
        email.value = 'user@poojamart.com';
        pass.value = 'user123';
        showToast('Demo User credentials filled', 'info');
    }
}

function fillDemoAdmin() {
    const email = document.getElementById('login-email');
    const pass = document.getElementById('login-password');
    if (email && pass) {
        email.value = 'admin@poojamart.com';
        pass.value = 'admin123';
        showToast('Demo Admin credentials filled', 'info');
    }
}

// Forgot password modal
async function handleForgotPassword() {
    const email = prompt('Enter your registered email address to receive password reset instructions:');
    if (!email) return;

    try {
        const res = await apiRequest('/auth/forgot-password', 'POST', { email });
        alert(res.message);
    } catch (err) {
        alert(err.message || 'Failed to process password request');
    }
}

// Profile page initialization
function initProfilePage() {
    const user = Storage.getUser();
    const profileForm = document.getElementById('profile-form');
    if (!profileForm) return;

    if (!user) {
        window.location.href = 'login.html?redirect=profile.html';
        return;
    }

    document.getElementById('prof-name').value = user.name || '';
    document.getElementById('prof-email').value = user.email || '';
    document.getElementById('prof-phone').value = user.phone || '';
    document.getElementById('prof-address').value = user.address || '';
    document.getElementById('prof-city').value = user.city || '';
    document.getElementById('prof-state').value = user.state || '';
    document.getElementById('prof-pin').value = user.postalCode || '';

    profileForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const updated = {
            name: document.getElementById('prof-name').value.trim(),
            phone: document.getElementById('prof-phone').value.trim(),
            address: document.getElementById('prof-address').value.trim(),
            city: document.getElementById('prof-city').value.trim(),
            state: document.getElementById('prof-state').value.trim(),
            postalCode: document.getElementById('prof-pin').value.trim(),
        };

        const newPass = document.getElementById('prof-password').value;
        if (newPass && newPass.trim().length >= 6) {
            updated.password = newPass.trim();
        }

        try {
            const res = await apiRequest(`/users/${user.id}`, 'PUT', updated);
            if (res.success && res.user) {
                Storage.setUser(res.user);
                showToast('Profile updated successfully!', 'success');
            }
        } catch (err) {
            showToast(err.message, 'error');
        }
    });
}
