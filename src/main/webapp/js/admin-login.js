document.getElementById('admin-login-form').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    
    // Using the same login API but we add a security check on the client side
    fetch('api/customers/login', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ email: email, password: password })
    })
    .then(async response => {
        if (!response.ok) {
            const err = await response.text();
            throw new Error(err || 'Login failed');
        }
        return response.json();
    })
    .then(user => {
        // Enforce Admin Access
        if (user.email === 'admin@techmart.com') {
            localStorage.setItem('techmart_user', JSON.stringify(user));
            window.location.href = 'admin.html'; // Redirect to dashboard
        } else {
            throw new Error('Access Denied. You are not an administrator.');
        }
    })
    .catch(error => {
        const errorDiv = document.getElementById('login-error');
        errorDiv.textContent = error.message;
        errorDiv.classList.remove('d-none');
    });
});
