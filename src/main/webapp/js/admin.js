document.addEventListener('DOMContentLoaded', () => {
    checkAdminAuth();
    
    // Initial fetches
    fetchOrders();
    fetchProducts();
    fetchMetrics();
    
    // Auto refresh metrics
    setInterval(fetchMetrics, 5000);
    
    // Add product form listener
    document.getElementById('product-form').addEventListener('submit', handleAddProduct);
});

function checkAdminAuth() {
    const user = JSON.parse(localStorage.getItem('techmart_user'));
    
    if (!user) {
        alert('Please login to access the admin dashboard.');
        window.location.href = 'login.html?redirect=admin.html';
        return;
    }
    
    // Security check: Must be logged in AND email must be admin@techmart.com
    if (user.email !== 'admin@techmart.com') {
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

// Tab Switching logic
// ----------------------------------------------------
function switchTab(tab) {
    // Update active class on buttons
    document.getElementById('tab-orders').classList.remove('active');
    document.getElementById('tab-products').classList.remove('active');
    document.getElementById('tab-metrics').classList.remove('active');
    document.getElementById(`tab-${tab}`).classList.add('active');
    
    // Toggle visibility of sections
    document.getElementById('section-orders').classList.add('d-none');
    document.getElementById('section-products').classList.add('d-none');
    document.getElementById('section-metrics').classList.add('d-none');
    document.getElementById(`section-${tab}`).classList.remove('d-none');
}

// ----------------------------------------------------
// Orders Logic
// ----------------------------------------------------
function fetchOrders() {
    const tbody = document.getElementById('orders-tbody');
    tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">Loading orders...</td></tr>';
    
    fetch('api/orders')
        .then(response => {
            if (!response.ok) throw new Error('Failed to load orders');
            return response.json();
        })
        .then(orders => {
            if (!orders || orders.length === 0) {
                tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted">No orders found.</td></tr>';
                return;
            }
            
            tbody.innerHTML = '';
            orders.forEach(order => {
                let badgeClass = 'bg-warning text-dark';
                if (order.status === 'SHIPPED') badgeClass = 'bg-info text-dark';
                if (order.status === 'DELIVERED') badgeClass = 'bg-success';
                if (order.status === 'CANCELLED') badgeClass = 'bg-danger';
                
                // Format the items list
                let itemsListHtml = '<ul class="list-unstyled mb-0 small">';
                if (order.items && order.items.length > 0) {
                    order.items.forEach(item => {
                        const prodName = item.product ? item.product.name : 'Unknown Product';
                        itemsListHtml += `<li>${item.quantity}x ${prodName}</li>`;
                    });
                } else {
                    itemsListHtml += '<li><span class="text-muted">No items</span></li>';
                }
                itemsListHtml += '</ul>';
                
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td class="fw-bold">#${order.id}</td>
                    <td>${new Date(order.orderDate).toLocaleString()}</td>
                    <td>${order.customer ? order.customer.name : 'Unknown'}<br><small class="text-muted">${order.customer ? order.customer.email : ''}</small></td>
                    <td>${order.shippingAddress}</td>
                    <td>${itemsListHtml}</td>
                    <td>
                        ${order.status === 'PENDING' ? `
                            <div class="dropdown">
                                <button class="btn btn-sm btn-warning dropdown-toggle fw-bold" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                    PENDING
                                </button>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item text-info fw-bold" href="#" onclick="updateOrderStatus(${order.id}, 'SHIPPED')"><i class="fas fa-truck me-2"></i> Mark as Shipped</a></li>
                                    <li><a class="dropdown-item text-danger fw-bold" href="#" onclick="updateOrderStatus(${order.id}, 'CANCELLED')"><i class="fas fa-times me-2"></i> Cancel Order</a></li>
                                </ul>
                            </div>
                        ` : order.status === 'SHIPPED' ? `
                            <div class="dropdown">
                                <button class="btn btn-sm btn-info dropdown-toggle fw-bold" type="button" data-bs-toggle="dropdown" aria-expanded="false">
                                    SHIPPED
                                </button>
                                <ul class="dropdown-menu">
                                    <li><a class="dropdown-item text-success fw-bold" href="#" onclick="updateOrderStatus(${order.id}, 'DELIVERED')"><i class="fas fa-check me-2"></i> Mark as Delivered</a></li>
                                    <li><a class="dropdown-item text-danger fw-bold" href="#" onclick="updateOrderStatus(${order.id}, 'CANCELLED')"><i class="fas fa-times me-2"></i> Cancel Order</a></li>
                                </ul>
                            </div>
                        ` : `
                            <span class="badge ${badgeClass}">${order.status}</span>
                        `}
                    </td>
                    <td class="text-end fw-bold">LKR ${order.totalAmount.toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error('Error fetching orders:', error);
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-danger">Error loading orders.</td></tr>';
        });
}

function updateOrderStatus(orderId, newStatus) {
    if (!confirm(`Are you sure you want to mark this order as ${newStatus}?`)) return;

    fetch(`api/orders/${orderId}/status?status=${newStatus}`, {
        method: 'PUT'
    })
    .then(response => {
        if (response.ok) {
            fetchOrders(); // Refresh table
        } else {
            alert('Failed to update order status.');
        }
    })
    .catch(err => console.error('Error updating order:', err));
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
                    <td class="text-center">
                        ${product.active !== false ? 
                            `<button class="btn btn-sm btn-success" onclick="toggleProductStatus(${product.id}, false)">Active</button>` : 
                            `<button class="btn btn-sm btn-danger" onclick="toggleProductStatus(${product.id}, true)">Inactive</button>`
                        }
                    </td>
                    <td class="text-end text-primary fw-bold">LKR ${product.price.toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
                    <td class="text-center">
                        <div class="d-flex gap-2 justify-content-center">
                            <button class="btn btn-sm btn-outline-info" onclick='openEditProductModal(${JSON.stringify(product).replace(/'/g, "&#39;")})'><i class="bi bi-pencil"></i></button>
                            <button class="btn btn-sm btn-outline-danger" onclick="deleteProduct(${product.id})"><i class="bi bi-trash"></i></button>
                        </div>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        })
        .catch(error => {
            console.error('Error fetching products:', error);
            tbody.innerHTML = '<tr><td colspan="8" class="text-center text-danger">Error loading products.</td></tr>';
        });
}

function toggleProductStatus(id, active) {
    if (!confirm(`Are you sure you want to mark this product as ${active ? 'Active' : 'Inactive'}?`)) return;

    fetch(`api/products/${id}/status?active=${active}`, {
        method: 'PUT'
    })
    .then(response => {
        if (response.ok) {
            fetchProducts();
        } else {
            alert('Failed to update product status.');
        }
    })
    .catch(err => console.error('Error:', err));
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

function deleteProduct(id) {
    if (confirm('Are you sure you want to delete this product? This action cannot be undone.')) {
        fetch(`api/products/${id}`, {
            method: 'DELETE'
        })
        .then(async response => {
            if (!response.ok) {
                const err = await response.text();
                throw new Error(err || 'Failed to delete product');
            }
            alert('Product deleted successfully!');
            fetchProducts();
        })
        .catch(error => {
            console.error('Error deleting product:', error);
            alert(error.message);
        });
    }
}

function handleAddProduct(event) {
    event.preventDefault();
    
    const productId = document.getElementById('prod-id').value;
    const isEdit = productId !== '';
    
    const name = document.getElementById('prod-name').value.trim();
    const category = document.getElementById('prod-category').value.trim();
    const desc = document.getElementById('prod-desc').value.trim();
    const price = parseFloat(document.getElementById('prod-price').value);
    const stock = parseInt(document.getElementById('prod-stock').value);
    const imageUrl = document.getElementById('prod-image').value.trim();

    // Validations
    if (!name || !category || !desc) {
        alert("Name, Category, and Description cannot be empty.");
        return;
    }
    
    if (isNaN(price) || price < 0) {
        alert("Price must be a valid positive number.");
        return;
    }
    
    if (isNaN(stock) || stock < 0) {
        alert("Stock quantity must be a valid positive number.");
        return;
    }

    const product = {
        name: name,
        category: category,
        description: desc,
        price: price,
        stockQuantity: stock,
        imageUrl: imageUrl || null
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

// Metrics Logic
// ----------------------------------------------------
function fetchMetrics() {
    fetch('api/metrics')
        .then(response => {
            if (!response.ok) throw new Error('Network response was not ok');
            return response.json();
        })
        .then(data => {
            document.getElementById('metric-active-users').textContent = data.activeUsers || 0;
            document.getElementById('metric-orders').textContent = data.totalOrdersProcessed || 0;
            document.getElementById('metric-avg-time').textContent = data.averageOrderProcessingTimeMs ? data.averageOrderProcessingTimeMs.toFixed(2) : '0';
            
            if (data.freeMemory) {
                const freeMB = (data.freeMemory / (1024 * 1024)).toFixed(0);
                document.getElementById('metric-memory').textContent = freeMB;
            }
            
            if (data.systemStartTime) {
                const startDate = new Date(data.systemStartTime);
                document.getElementById('metric-started').textContent = startDate.toLocaleString();
            }
        })
        .catch(error => {
            console.error('Error fetching metrics:', error);
        });
}
