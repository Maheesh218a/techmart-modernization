let cart = JSON.parse(localStorage.getItem('techmart_cart')) || [];

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    renderCart();
    
    document.getElementById('checkout-form').addEventListener('submit', handleCheckout);
});

function checkAuth() {
    const user = JSON.parse(localStorage.getItem('techmart_user'));
    if (user) {
        document.getElementById('login-menu').classList.add('d-none');
        document.getElementById('user-menu').classList.remove('d-none');
        const adminBtn = document.getElementById('nav-admin-btn');
        if (user.email === 'admin@techmart.com') {
            document.getElementById('nav-username').textContent = 'Admin';
            if (adminBtn) adminBtn.classList.remove('d-none');
        } else {
            document.getElementById('nav-username').textContent = user.name;
            if (adminBtn) adminBtn.classList.add('d-none');
        }
    } else {
        document.getElementById('login-menu').classList.remove('d-none');
        document.getElementById('user-menu').classList.add('d-none');
    }
}

function logout() {
    fetch('api/customers/logout', { method: 'POST' }).finally(() => {
        localStorage.removeItem('techmart_user');
        checkAuth();
        // Redirect to login if on cart page
        window.location.href = 'login.html?redirect=cart.html';
    });
}

function renderCart() {
    const tbody = document.getElementById('cart-items-body');
    const checkoutBtn = document.getElementById('btn-checkout');
    
    if (cart.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center py-5">
                    <i class="bi bi-cart-x text-muted" style="font-size: 3rem;"></i>
                    <h5 class="mt-3 text-muted">Your cart is empty</h5>
                    <a href="index.html" class="btn btn-outline-primary mt-2">Start Shopping</a>
                </td>
            </tr>
        `;
        updateTotals(0);
        checkoutBtn.disabled = true;
        return;
    }

    checkoutBtn.disabled = false;
    tbody.innerHTML = '';
    let subtotal = 0;

    cart.forEach((item, index) => {
        const itemTotal = item.unitPrice * item.quantity;
        subtotal += itemTotal;
        
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td class="ps-4 py-3">
                <div class="d-flex align-items-center">
                    <div class="bg-light rounded p-2 me-3 text-center" style="width: 50px; height: 50px;">
                        <i class="bi bi-box text-secondary fs-4"></i>
                    </div>
                    <div>
                        <h6 class="mb-0 fw-bold">${item.name}</h6>
                        <small class="text-muted">Product ID: ${item.productId}</small>
                    </div>
                </div>
            </td>
            <td class="py-3 align-middle">LKR ${item.unitPrice.toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
            <td class="py-3 align-middle">
                <div class="d-flex align-items-center" style="width: 120px;">
                    <button type="button" class="btn btn-sm btn-outline-secondary" onclick="updateQuantity(${index}, -1)">-</button>
                    <input type="text" class="form-control form-control-sm text-center mx-2" value="${item.quantity}" readonly>
                    <button type="button" class="btn btn-sm btn-outline-secondary" onclick="updateQuantity(${index}, 1)">+</button>
                </div>
            </td>
            <td class="py-3 align-middle fw-bold text-dark">LKR ${itemTotal.toLocaleString(undefined, {minimumFractionDigits: 2})}</td>
            <td class="text-center pe-4 py-3 align-middle">
                <button type="button" class="btn btn-sm btn-outline-danger" onclick="removeItem(${index})">
                    <i class="bi bi-trash"></i>
                </button>
            </td>
        `;
        tbody.appendChild(tr);
    });

    updateTotals(subtotal);
}

function updateQuantity(index, change) {
    const item = cart[index];
    const newQty = item.quantity + change;
    
    if (newQty > 0) {
        item.quantity = newQty;
    } else {
        // Remove if 0
        cart.splice(index, 1);
    }
    
    saveCart();
    renderCart();
}

function removeItem(index) {
    cart.splice(index, 1);
    saveCart();
    renderCart();
}

function saveCart() {
    localStorage.setItem('techmart_cart', JSON.stringify(cart));
    
    // Sync to backend if logged in
    const user = JSON.parse(localStorage.getItem('techmart_user'));
    if (user) {
        const payload = {
            items: cart.map(item => ({
                productId: item.productId,
                quantity: item.quantity
            }))
        };
        fetch(`api/cart/${user.id}/sync`, {
            method: 'POST',
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(payload)
        }).catch(err => console.error("Failed to sync cart", err));
    }
}

function updateTotals(subtotal) {
    const formattedTotal = 'LKR ' + subtotal.toLocaleString(undefined, {minimumFractionDigits: 2});
    document.getElementById('summary-subtotal').textContent = formattedTotal;
    document.getElementById('summary-total').textContent = formattedTotal;
}

function handleCheckout(e) {
    e.preventDefault();
    
    if (cart.length === 0) return;

    const user = JSON.parse(localStorage.getItem('techmart_user'));
    if (!user) {
        window.location.href = 'login.html?redirect=cart.html';
        return;
    }

    const address = document.getElementById('shipping-address').value;
    const notes = document.getElementById('order-notes').value;
    
    const btn = document.getElementById('btn-checkout');
    const originalText = btn.innerHTML;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...';
    btn.disabled = true;

    // Prepare JSON payload matching OrderResource.OrderRequest structure
    const payload = {
        customerId: user.id,
        shippingAddress: address,
        notes: notes,
        items: cart.map(item => ({
            product: { id: item.productId },
            quantity: item.quantity,
            unitPrice: item.unitPrice
        }))
    };

    // Call REST API
    fetch('api/orders', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(err => { throw new Error(err || 'Failed to place order') });
        }
        return response.json();
    })
    .then(order => {
        // Success
        cart = [];
        saveCart();
        
        document.getElementById('order-success-msg').innerHTML = 
            `Your order <b>#${order.id}</b> was successfully placed!<br>A JMS background notification has been triggered.`;
            
        const modal = new bootstrap.Modal(document.getElementById('successModal'));
        modal.show();
    })
    .catch(error => {
        console.error('Checkout error:', error);
        alert('Failed to place order: ' + error.message);
        btn.innerHTML = originalText;
        btn.disabled = false;
    });
}
