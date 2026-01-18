document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('.consulta-form');
    const submitButton = form.querySelector('.submit-button');
    
    if (form) {
        initFloatingLabels();
        
        form.addEventListener('submit', function(e) {
            if (!validateForm()) {
                e.preventDefault();
                return;
            }
            
            submitButton.classList.add('loading');
            submitButton.innerHTML = '<i class="ri-loader-4-line"></i> Enviando...';
            submitButton.disabled = true;
        });
        
        form.addEventListener('input', function(e) {
            validateField(e.target);
        });
        
        form.addEventListener('change', function(e) {
            validateField(e.target);
        });
    }
    
    function initFloatingLabels() {
        const inputs = form.querySelectorAll('input, select, textarea');
        
        inputs.forEach(input => {
            if (input.value.trim() !== '' || input.hasAttribute('placeholder-shown')) {
                const label = input.nextElementSibling;
                if (label && label.tagName === 'LABEL') {
                    label.classList.add('floating');
                }
            }
            
            input.addEventListener('focus', function() {
                const label = this.nextElementSibling;
                if (label && label.tagName === 'LABEL') {
                    label.classList.add('floating');
                }
            });
            
            input.addEventListener('blur', function() {
                const label = this.nextElementSibling;
                if (label && label.tagName === 'LABEL') {
                    if (this.value.trim() === '' && !this.hasAttribute('placeholder-shown')) {
                        label.classList.remove('floating');
                    }
                }
            });
        });
    }
    
    function validateField(field) {
        if (!field.hasAttribute('required')) return true;
        
        const value = field.value.trim();
        const errorMessage = field.parentNode.querySelector('.error-message');
        
        if (errorMessage) {
            errorMessage.remove();
        }
        
        let isValid = true;
        let message = '';
        
        if (value === '') {
            isValid = false;
            message = 'Este campo es obligatorio';
        } else if (field.type === 'email') {
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(value)) {
                isValid = false;
                message = 'Por favor, introduce un email válido';
            }
        }
        
        if (!isValid) {
            field.style.borderColor = '#ff4757';
            field.style.backgroundColor = 'rgba(255, 71, 87, 0.05)';
            
            const errorDiv = document.createElement('div');
            errorDiv.className = 'error-message';
            errorDiv.style.color = '#ff4757';
            errorDiv.style.fontSize = '0.8rem';
            errorDiv.style.marginTop = '0.5rem';
            errorDiv.textContent = message;
            
            field.parentNode.appendChild(errorDiv);
        } else {
            field.style.borderColor = '';
            field.style.backgroundColor = '';
        }
        
        return isValid;
    }
    
    function validateForm() {
        const requiredFields = form.querySelectorAll('[required]');
        let isValid = true;
        
        requiredFields.forEach(field => {
            if (!validateField(field)) {
                isValid = false;
                
                if (isValid === false) {
                    field.focus();
                    isValid = true;
                }
            }
        });
        
        return isValid;
    }
    
    const formSections = document.querySelectorAll('.form-section');
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -50px 0px'
    };
    
    const observer = new IntersectionObserver(function(entries) {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.style.opacity = '1';
                entry.target.style.transform = 'translateY(0)';
            }
        });
    }, observerOptions);
    
    formSections.forEach(section => {
        section.style.opacity = '0';
        section.style.transform = 'translateY(20px)';
        section.style.transition = 'opacity 0.5s ease, transform 0.5s ease';
        observer.observe(section);
    });
});