document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('register-form').addEventListener('submit', handleRegister);
});

function handleRegister(e) {
    e.preventDefault();
    
    const name = document.getElementById('name').value;
    const phone = document.getElementById('phone').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const address = document.getElementById('address').value;
    
    const errorDiv = document.getElementById('register-error');
    const btn = document.getElementById('btn-register');
    
    const originalText = btn.innerHTML;
    btn.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Registering...';
    btn.disabled = true;
    errorDiv.classList.add('d-none');

    const payload = {
        name: name,
        phone: phone,
        email: email,
        password: password,
        address: address,
        active: true
    };

    fetch('api/customers', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (!response.ok) {
            return response.text().then(err => { throw new Error(err || 'Registration failed') });
        }
        return response.json();
    })
    .then(customer => {
        // Automatically log them in by saving to localStorage
        localStorage.setItem('techmart_user', JSON.stringify(customer));
        
        alert('Registration successful! Welcome to TechMart.');
        window.location.href = 'index.html';
    })
    .catch(error => {
        errorDiv.textContent = error.message;
        errorDiv.classList.remove('d-none');
        btn.innerHTML = originalText;
        btn.disabled = false;
    });
}
