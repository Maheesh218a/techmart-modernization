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
    .then(customer => {
        // Save user to localStorage
        localStorage.setItem('techmart_user', JSON.stringify(customer));
        
        // Redirect back to original page or home
        const urlParams = new URLSearchParams(window.location.search);
        const redirect = urlParams.get('redirect') || 'index.html';
        window.location.href = redirect;
    })
    .catch(error => {
        errorDiv.textContent = error.message;
        errorDiv.classList.remove('d-none');
        btn.innerHTML = originalText;
        btn.disabled = false;
    });
}
