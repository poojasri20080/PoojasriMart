/**
 * Home Page Logic
 */
document.addEventListener('DOMContentLoaded', () => {
    initHeroSlider();
    loadHomeData();
});

// Hero Banner Slider
function initHeroSlider() {
    const slides = document.querySelectorAll('.hero-slide');
    if (!slides.length) return;

    let current = 0;
    const total = slides.length;

    function showSlide(index) {
        slides.forEach(s => s.classList.remove('active'));
        slides[index].classList.add('active');
    }

    const nextBtn = document.querySelector('.slider-btn.next');
    const prevBtn = document.querySelector('.slider-btn.prev');

    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            current = (current + 1) % total;
            showSlide(current);
        });
    }

    if (prevBtn) {
        prevBtn.addEventListener('click', () => {
            current = (current - 1 + total) % total;
            showSlide(current);
        });
    }

    setInterval(() => {
        current = (current + 1) % total;
        showSlide(current);
    }, 5000);
}

// Load Home Page Data
async function loadHomeData() {
    try {
        // 1. Categories
        const categories = await apiRequest('/categories');
        renderCategoriesStrip(categories);

        // 2. Featured
        const featured = await apiRequest('/products/featured');
        renderProductsGrid('featured-products-container', featured.slice(0, 8));

        // 3. Best Sellers
        const bestSellers = await apiRequest('/products/best-sellers');
        renderProductsGrid('bestseller-products-container', bestSellers.slice(0, 8));

        // 4. New Arrivals
        const newArrivals = await apiRequest('/products/new-arrivals');
        renderProductsGrid('new-arrivals-container', newArrivals.slice(0, 8));

    } catch (err) {
        console.error('Failed to load home page products:', err);
    }
}

function renderCategoriesStrip(categories) {
    const strip = document.getElementById('category-nav-strip');
    if (!strip) return;

    strip.innerHTML = categories.map(cat => `
        <a href="products.html?categoryId=${cat.id}" class="category-nav-item">
            <span class="cat-icon">${cat.icon || '🧸'}</span>
            <span>${cat.name}</span>
        </a>
    `).join('');
}

function renderProductsGrid(containerId, products) {
    const container = document.getElementById(containerId);
    if (!container) return;

    if (!products || products.length === 0) {
        container.innerHTML = `<p style="padding:20px;color:#878787;">No products available at the moment.</p>`;
        return;
    }

    container.innerHTML = products.map(product => createProductCardHtml(product)).join('');
}

function createProductCardHtml(p) {
    const disc = p.discountPercent || (p.originalPrice ? Math.round(((p.originalPrice - p.price)/p.originalPrice)*100) : 0);
    return `
        <div class="product-card" onclick="window.location.href='product-detail.html?id=${p.id}'">
            <div class="product-badge-group">
                ${p.bestSeller ? '<span class="badge-tag tag-bestseller">Best Seller</span>' : ''}
                ${p.newArrival ? '<span class="badge-tag tag-new">New</span>' : ''}
                ${p.featured ? '<span class="badge-tag tag-featured">Featured</span>' : ''}
            </div>
            <div class="product-img-wrapper">
                <img src="${p.imageUrl}" alt="${p.name}" loading="lazy">
            </div>
            <h3 class="product-title" title="${p.name}">${p.name}</h3>
            <div class="product-rating-row">
                <span class="badge-rating">${p.rating || '4.5'} ★</span>
                <span class="rating-count">(${p.reviewCount || 100})</span>
                <span class="fa-badge">✓ Assured</span>
            </div>
            <div class="product-price-row">
                <span class="current-price">₹${p.price.toLocaleString('en-IN')}</span>
                ${p.originalPrice ? `<span class="original-price">₹${p.originalPrice.toLocaleString('en-IN')}</span>` : ''}
                ${disc > 0 ? `<span class="discount-tag">${disc}% off</span>` : ''}
            </div>
            <div class="delivery-tag">
                Free delivery over <span>₹499</span>
            </div>
            <div class="product-card-actions" onclick="event.stopPropagation()">
                <button class="btn btn-cart btn-sm btn-block" onclick="quickAddToCart(${p.id})">
                    🛒 Add to Cart
                </button>
            </div>
        </div>
    `;
}

// Quick Add to Cart
async function quickAddToCart(productId) {
    const user = Storage.getUser();
    if (!user) {
        // Fallback for guest: add to localCart and show notification
        const localCart = Storage.getLocalCart();
        const existing = localCart.find(it => it.productId === productId);
        if (existing) {
            existing.quantity += 1;
        } else {
            localCart.push({ productId, quantity: 1 });
        }
        Storage.setLocalCart(localCart);
        updateCartCountBadge();
        showToast('Added to cart! Log in anytime to save your cart permanently.', 'success');
        return;
    }

    try {
        await apiRequest('/cart/add', 'POST', {
            userId: user.id,
            productId: productId,
            quantity: 1
        });
        showToast('Added to your PoojaMart Cart!', 'success');
        updateCartCountBadge();
    } catch (err) {
        showToast(err.message || 'Failed to add item to cart', 'error');
    }
}
