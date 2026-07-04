document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('login-form').addEventListener('submit', handleLogin);
});

function handleLogin(e) {
    e.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const errorDiv = document.getElementById('login-error');
    const btn = document.getElementById('btn-login');
    
    const originalText = btn.innerHTML;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Logging in...';
    btn.disabled = true;
    errorDiv.classList.add('d-none');

    const payload = {
        email: email,
        password: password
    };

    fetch('api/customers/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(err => { throw new Error(err || 'Invalid credentials') });
        }
        return response.json();
    })
    .then(data => {
        // Save user to localStorage
        localStorage.setItem('techmart_user', JSON.stringify(data.customer));
        localStorage.setItem('techmart_session', data.sessionId);
        
        // Merge carts
        let localCart = JSON.parse(localStorage.getItem('techmart_cart')) || [];
        
        fetch(`api/cart/${data.customer.id}`)
            .then(res => res.ok ? res.json() : { items: [] })
            .then(dbCart => {
                let mergedCartMap = new Map();
                
                // Add DB items
                if (dbCart.items) {
                    dbCart.items.forEach(i => {
                        mergedCartMap.set(i.product.id, {
                            productId: i.product.id,
                            name: i.product.name,
                            unitPrice: i.product.price, // API returns price inside product
                            quantity: i.quantity
                        });
                    });
                }
                
                // Add Local items (add quantities for same product)
                localCart.forEach(i => {
                    if (mergedCartMap.has(i.productId)) {
                        mergedCartMap.get(i.productId).quantity += i.quantity;
                    } else {
                        mergedCartMap.set(i.productId, i);
                    }
                });
                
                const finalCart = Array.from(mergedCartMap.values());
                localStorage.setItem('techmart_cart', JSON.stringify(finalCart));
                
                // Sync back to DB
                const payload = {
                    items: finalCart.map(item => ({
                        productId: item.productId,
                        quantity: item.quantity
                    }))
                };
                
                return fetch(`api/cart/${customer.id}/sync`, {
                    method: 'POST',
                    headers: {'Content-Type': 'application/json'},
                    body: JSON.stringify(payload)
                });
            })
            .finally(() => {
                // Redirect back to original page or home
                const urlParams = new URLSearchParams(window.location.search);
                const redirect = urlParams.get('redirect') || 'index.html';
                window.location.href = redirect;
            });
    })
    .catch(error => {
        errorDiv.textContent = error.message;
        errorDiv.classList.remove('d-none');
        btn.innerHTML = originalText;
        btn.disabled = false;
    });
}
