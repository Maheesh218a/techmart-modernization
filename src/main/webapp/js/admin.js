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
                    <td class="text-center">
                        <button class="btn btn-sm btn-outline-info" onclick='openEditProductModal(${JSON.stringify(product).replace(/'/g, "&#39;")})'><i class="bi bi-pencil"></i></button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error('Error fetching products:', error);
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-danger">Error loading products.</td></tr>';
        });
}

function openAddProductModal() {
    document.getElementById('product-form').reset();
    document.getElementById('prod-id').value = '';
    document.getElementById('productModalTitle').textContent = 'Add New Product';
    document.getElementById('productModalSubmitBtn').textContent = 'Save Product';
    new bootstrap.Modal(document.getElementById('productModal')).show();
}

function openEditProductModal(product) {
    document.getElementById('product-form').reset();
    document.getElementById('prod-id').value = product.id;
    document.getElementById('prod-name').value = product.name;
    document.getElementById('prod-category').value = product.category;
    document.getElementById('prod-desc').value = product.description;
    document.getElementById('prod-price').value = product.price;
    document.getElementById('prod-stock').value = product.stockQuantity;
    document.getElementById('prod-image').value = product.imageUrl || '';
    
    document.getElementById('productModalTitle').textContent = 'Edit Product';
    document.getElementById('productModalSubmitBtn').textContent = 'Update Product';
    new bootstrap.Modal(document.getElementById('productModal')).show();
}

function handleAddProduct(event) {
    event.preventDefault();
    
    const productId = document.getElementById('prod-id').value;
    const isEdit = productId !== '';
    
    const product = {
        name: document.getElementById('prod-name').value,
        category: document.getElementById('prod-category').value,
        description: document.getElementById('prod-desc').value,
        price: parseFloat(document.getElementById('prod-price').value),
        stockQuantity: parseInt(document.getElementById('prod-stock').value),
        imageUrl: document.getElementById('prod-image').value || null
    };
    
    const url = isEdit ? `api/products/${productId}` : 'api/products';
    const method = isEdit ? 'PUT' : 'POST';
    
    fetch(url, {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(product)
    })
    .then(async response => {
        if (!response.ok) {
            const err = await response.text();
            throw new Error(err || 'Failed to save product');
        }
        return response.json();
    })
    .then(savedProduct => {
        alert(isEdit ? 'Product updated successfully!' : 'Product added successfully!');
        const modal = bootstrap.Modal.getInstance(document.getElementById('productModal'));
        modal.hide();
        document.getElementById('product-form').reset();
        fetchProducts();
    })
    .catch(error => {
        console.error('Error saving product:', error);
        alert(error.message);
    });
}
