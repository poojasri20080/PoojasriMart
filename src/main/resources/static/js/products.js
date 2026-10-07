/**
 * Product Catalog, Search & Filtering Logic
 */
let currentCategory = null;
let currentKeyword = '';
let currentSort = 'newest';
let currentMinPrice = null;
let currentMaxPrice = null;
let currentPage = 0;

document.addEventListener('DOMContentLoaded', () => {
    parseUrlParams();
    loadCategoriesForFilter();
    loadProducts();
});

function parseUrlParams() {
    const params = new URLSearchParams(window.location.search);
    if (params.has('categoryId')) {
        currentCategory = params.get('categoryId');
    }
    if (params.has('keyword')) {
        currentKeyword = params.get('keyword');
        const searchInput = document.getElementById('global-search-input');
        if (searchInput) searchInput.value = currentKeyword;
    }
    if (params.has('sort')) {
        currentSort = params.get('sort');
    }
}

async function loadCategoriesForFilter() {
    try {
        const categories = await apiRequest('/categories');
        const filterBox = document.getElementById('category-filter-list');
        if (!filterBox) return;

        filterBox.innerHTML = `
            <label class="filter-radio-label">
                <input type="radio" name="catFilter" value="" ${!currentCategory ? 'checked' : ''} onchange="filterByCategory(null)">
                All Categories
            </label>
        ` + categories.map(c => `
            <label class="filter-radio-label">
                <input type="radio" name="catFilter" value="${c.id}" ${currentCategory == c.id ? 'checked' : ''} onchange="filterByCategory(${c.id})">
                ${c.icon || ''} ${c.name}
            </label>
        `).join('');
    } catch (e) {
        console.error('Error loading categories:', e);
    }
}

async function loadProducts() {
    const grid = document.getElementById('catalog-products-grid');
    const countDisplay = document.getElementById('products-count-label');
    const headerTitle = document.getElementById('catalog-header-title');

    if (!grid) return;
    grid.innerHTML = '<div style="padding:40px;text-align:center;color:#878787;">Loading toys...</div>';

    let url = `/products?sortBy=${currentSort}&page=${currentPage}&size=60`;
    if (currentCategory) url += `&categoryId=${currentCategory}`;
    if (currentKeyword) url += `&keyword=${encodeURIComponent(currentKeyword)}`;
    if (currentMinPrice !== null) url += `&minPrice=${currentMinPrice}`;
    if (currentMaxPrice !== null) url += `&maxPrice=${currentMaxPrice}`;

    try {
        const data = await apiRequest(url);
        const products = data.products || [];

        if (countDisplay) {
            countDisplay.textContent = `(Showing ${products.length} toys)`;
        }
        if (headerTitle) {
            if (currentKeyword) {
                headerTitle.textContent = `Search results for "${currentKeyword}"`;
            } else if (currentCategory) {
                headerTitle.textContent = `Toys in Selected Category`;
            } else {
                headerTitle.textContent = `All Toys Collection`;
            }
        }

        if (products.length === 0) {
            grid.innerHTML = `
                <div style="grid-column: 1 / -1; padding: 60px 20px; text-align: center; background: #fff; border-radius: 4px;">
                    <div style="font-size: 48px; margin-bottom: 12px;">🧸</div>
                    <h3 style="font-size: 18px; margin-bottom: 8px;">No toys match your filter</h3>
                    <p style="color: #878787; font-size: 14px; margin-bottom: 16px;">Try clearing filters or searching with another keyword.</p>
                    <button class="btn btn-primary btn-sm" onclick="clearAllFilters()">Reset All Filters</button>
                </div>
            `;
            return;
        }

        grid.innerHTML = products.map(p => createProductCardHtml(p)).join('');
    } catch (err) {
        grid.innerHTML = `<div style="padding:40px;text-align:center;color:#d32f2f;">Failed to load products. Please check if the server is running.</div>`;
    }
}

function filterByCategory(catId) {
    currentCategory = catId;
    currentPage = 0;
    loadProducts();
}

function filterByPrice(min, max) {
    currentMinPrice = min;
    currentMaxPrice = max;
    currentPage = 0;
    loadProducts();
}

function changeSort(sortValue) {
    currentSort = sortValue;
    // update active tab
    document.querySelectorAll('.sort-tab').forEach(t => {
        t.classList.remove('active');
        if (t.dataset.sort === sortValue) t.classList.add('active');
    });
    currentPage = 0;
    loadProducts();
}

function clearAllFilters() {
    currentCategory = null;
    currentKeyword = '';
    currentMinPrice = null;
    currentMaxPrice = null;
    currentSort = 'newest';

    const radios = document.querySelectorAll('input[name="catFilter"]');
    radios.forEach(r => r.checked = (r.value === ''));

    const priceRadios = document.querySelectorAll('input[name="priceFilter"]');
    priceRadios.forEach(r => r.checked = (r.value === 'all'));

    const searchInput = document.getElementById('global-search-input');
    if (searchInput) searchInput.value = '';

    loadProducts();
}
