document.addEventListener('DOMContentLoaded', () => {
    checkAdminAuth();
    
    // Initial fetches
    fetchOrders();
    fetchProducts();
    
    // Add product form listener
    document.getElementById('add-product-form').addEventListener('submit', handleAddProduct);
});

function checkAdminAuth() {
    const user = JSON.parse(localStorage.getItem('techmart_user'));
    
    // Security check: Must be logged in AND email must be admin@techmart.com
    if (!user || user.email !== 'admin@techmart.com') {
        alert('Access Denied: You do not have administrator privileges.');
        window.location.href = 'index.html';
        return;
    }
    
    document.getElementById('nav-username').textContent = user.name;
}

function logout() {
    localStorage.removeItem('techmart_user');
    window.location.href = 'index.html';
}

function switchTab(tab) {
    // Update active class on buttons
    document.getElementById('tab-orders').classList.remove('active');
    document.getElementById('tab-products').classList.remove('active');
    document.getElementById(`tab-${tab}`).classList.add('active');
    
    // Toggle visibility of sections
    document.getElementById('section-orders').classList.add('d-none');
    document.getElementById('section-products').classList.add('d-none');
    document.getElementById(`section-${tab}`).classList.remove('d-none');
}

// ----------------------------------------------------
// Orders Logic
// ----------------------------------------------------
function fetchOrders() {
    const tbody = document.getElementById('orders-tbody');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Loading orders...</td></tr>';
    
    fetch('api/orders')
        .then(response => {
            if (!response.ok) throw new Error('Failed to load orders');
            return response.json();
        })
        .then(orders => {
            if (!orders || orders.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No orders found.</td></tr>';
                return;
            }
            
            tbody.innerHTML = '';
            orders.forEach(order => {
                let badgeClass = 'bg-warning text-dark';
                if (order.status === 'DELIVERED') badgeClass = 'bg-success';
                if (order.status === 'CANCELLED') badgeClass = 'bg-danger';
                
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="fw-bold">#${order.id}</td>
                    <td>${new Date(order.orderDate).toLocaleString()}</td>
                    <td>${order.customer ? order.customer.name : 'Unknown'}<br><small class="text-muted">${order.customer ? order.customer.email : ''}</small></td>
                    <td>${order.shippingAddress}</td>
                    <td><span class="badge ${badgeClass}">${order.status}</span></td>
                    <td class="text-end fw-bold">LKR ${order.totalAmount.toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error('Error fetching orders:', error);
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Error loading orders.</td></tr>';
        });
}

// ----------------------------------------------------
// Products Logic
// ----------------------------------------------------
function fetchProducts() {
    const tbody = document.getElementById('products-tbody');
    tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">Loading products...</td></tr>';
    
    fetch('api/products')
        .then(response => {
            if (!response.ok) throw new Error('Failed to load products');
            return response.json();
        })
        .then(products => {
            if (!products || products.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted">No products found.</td></tr>';
                return;
            }
            
            tbody.innerHTML = '';
            products.forEach(product => {
                let imageHtml = product.imageUrl 
                    ? `<img src="${product.imageUrl}" alt="${product.name}" style="height: 40px; width: 40px; object-fit: cover;" class="rounded">` 
                    : '<div class="bg-secondary rounded d-inline-flex align-items-center justify-content-center" style="height: 40px; width: 40px;"><i class="bi bi-image text-white"></i></div>';
                    
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td>${product.id}</td>
                    <td>${imageHtml}</td>
                    <td class="fw-bold">${product.name}</td>
                    <td><span class="badge bg-secondary">${product.category}</span></td>
                    <td>${product.stockQuantity}</td>
                    <td class="text-end text-primary fw-bold">LKR ${product.price.toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error('Error fetching products:', error);
            tbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger">Error loading products.</td></tr>';
        });
}

function handleAddProduct(event) {
    event.preventDefault();
    
    const product = {
        name: document.getElementById('prod-name').value,
        category: document.getElementById('prod-category').value,
        description: document.getElementById('prod-desc').value,
        price: parseFloat(document.getElementById('prod-price').value),
        stockQuantity: parseInt(document.getElementById('prod-stock').value),
        imageUrl: document.getElementById('prod-image').value || null
    };
    
    fetch('api/products', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(product)
    })
    .then(async response => {
        if (!response.ok) {
            const err = await response.text();
            throw new Error(err || 'Failed to add product');
        }
        return response.json();
    })
    .then(newProduct => {
        alert('Product added successfully!');
        // Close modal
        const modal = bootstrap.Modal.getInstance(document.getElementById('addProductModal'));
        modal.hide();
        // Reset form
        document.getElementById('add-product-form').reset();
        // Refresh table
        fetchProducts();
    })
    .catch(error => {
        console.error('Error adding product:', error);
        alert(error.message);
    });
}
