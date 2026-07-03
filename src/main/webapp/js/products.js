// Array to store cart items
let cart = JSON.parse(localStorage.getItem('techmart_cart')) || [];

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    updateCartBadge();
    fetchProducts();
});

function checkAuth() {
    const user = JSON.parse(localStorage.getItem('techmart_user'));
    if (user) {
        document.getElementById('login-menu').classList.add('d-none');
        document.getElementById('user-menu').classList.remove('d-none');
        document.getElementById('nav-username').textContent = user.name;
    } else {
        document.getElementById('login-menu').classList.remove('d-none');
        document.getElementById('user-menu').classList.add('d-none');
    }
}

function logout() {
    localStorage.removeItem('techmart_user');
    checkAuth();
}

function fetchProducts() {
    // Call the REST API we built
    fetch('api/products')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(products => {
            displayProducts(products);
        })
        .catch(error => {
            console.error('Error fetching products:', error);
            document.getElementById('loading-spinner').innerHTML = 
                '<div class="alert alert-danger">Failed to load products from server. Make sure the backend is running.</div>';
        });
}

function displayProducts(products) {
    const productList = document.getElementById('product-list');
    const loadingSpinner = document.getElementById('loading-spinner');
    
    // Hide spinner
    if (loadingSpinner) loadingSpinner.style.display = 'none';

    if (!products || products.length === 0) {
        productList.innerHTML = '<div class="col-12 text-center"><p class="text-muted">No products available at the moment.</p></div>';
        return;
    }

    productList.innerHTML = ''; // Clear container

    products.forEach(product => {
        // Create a beautiful Bootstrap card for each product
        const card = document.createElement('div');
        card.className = 'col';
        card.innerHTML = `
            <div class="card h-100 shadow-sm product-card border-0">
                <div class="card-img-top bg-light d-flex align-items-center justify-content-center" style="height: 200px; font-size: 3rem; color: #adb5bd;">
                    <i class="bi ${getIconForCategory(product.category)}"></i>
                </div>
                <div class="card-body d-flex flex-column">
                    <span class="badge bg-secondary mb-2 align-self-start">${product.category}</span>
                    <h5 class="card-title fw-bold text-dark">${product.name}</h5>
                    <p class="card-text text-muted small flex-grow-1">${product.description}</p>
                    <div class="mt-auto">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h4 class="mb-0 text-primary fw-bold">LKR ${product.price.toLocaleString(undefined, {minimumFractionDigits: 2})}</h4>
                            <small class="text-muted">Stock: ${product.stockQuantity}</small>
                        </div>
                        <button class="btn btn-primary w-100 shadow-sm" onclick="addToCart(${product.id}, '${product.name}', ${product.price}, ${product.stockQuantity})">
                            <i class="bi bi-cart-plus"></i> Add to Cart
                        </button>
                    </div>
                </div>
            </div>
        `;
        productList.appendChild(card);
    });
}

// Helper to return a nice icon based on category
function getIconForCategory(category) {
    if (!category) return 'bi-box';
    const cat = category.toLowerCase();
    if (cat.includes('laptop') || cat.includes('computer')) return 'bi-laptop';
    if (cat.includes('phone') || cat.includes('mobile')) return 'bi-phone';
    if (cat.includes('audio') || cat.includes('headphone')) return 'bi-headphones';
    if (cat.includes('mouse') || cat.includes('keyboard')) return 'bi-mouse';
    return 'bi-box';
}

function addToCart(id, name, price, stock) {
    const user = JSON.parse(localStorage.getItem('techmart_user'));
    if (!user) {
        alert('Please login to add items to your cart.');
        window.location.href = 'login.html?redirect=index.html';
        return;
    }

    if (stock <= 0) {
        alert('Sorry, this product is out of stock!');
        return;
    }
    
    // Check if product already in cart
    const existingItem = cart.find(item => item.productId === id);
    if (existingItem) {
        if (existingItem.quantity >= stock) {
            alert('Cannot add more of this item. Stock limit reached.');
            return;
        }
        existingItem.quantity += 1;
    } else {
        cart.push({
            productId: id,
            name: name,
            unitPrice: price,
            quantity: 1
        });
    }

    // Save to localStorage
    localStorage.setItem('techmart_cart', JSON.stringify(cart));
    
    updateCartBadge();
    
    // Simple visual feedback (a real app might use a toast notification)
    alert(name + " added to cart!");
}

function updateCartBadge() {
    const badge = document.getElementById('cart-count');
    if (badge) {
        const totalItems = cart.reduce((total, item) => total + item.quantity, 0);
        badge.textContent = totalItems;
    }
}
