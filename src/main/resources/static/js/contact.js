// contact.js - Versión corregida para el problema del scroll
class ContactFormAnimations {
    constructor() {
        this.form = document.querySelector('.contact-form');
        this.submitButton = document.querySelector('.submit-button');
        this.init();
    }

    init() {
        this.fixScrollIssues();
        this.setupFormInteractions();
        this.setupInputAnimations();
        this.setupFormValidation();
    }

    fixScrollIssues() {
        // Asegurar que la página pueda hacer scroll
        document.body.style.overflowX = 'hidden';
        document.body.style.position = 'relative';

        // Asegurar que el video no interfiera con el scroll
        const videoBg = document.querySelector('.contact-video-bg');
        if (videoBg) {
            videoBg.style.pointerEvents = 'none';
        }
    }

    setupFormInteractions() {
        if (!this.form) return;

        // Form submission
        this.form.addEventListener('submit', (e) => {
            this.handleFormSubmit(e);
        });

        // Input focus effects
        const inputs = this.form.querySelectorAll('input, select, textarea');
        inputs.forEach(input => {
            input.addEventListener('focus', this.handleInputFocus.bind(this));
            input.addEventListener('blur', this.handleInputBlur.bind(this));
        });
    }

    setupInputAnimations() {
        // Add floating label functionality
        const formGroups = document.querySelectorAll('.form-group');

        formGroups.forEach(group => {
            const input = group.querySelector('input, select, textarea');
            const label = group.querySelector('label');

            if (!input || !label) return;

            // Check if input has content on load
            if (input.value) {
                label.classList.add('floating');
            }

            input.addEventListener('input', () => {
                if (input.value) {
                    label.classList.add('floating');
                } else {
                    label.classList.remove('floating');
                }
            });
        });
    }

    setupFormValidation() {
        const inputs = this.form.querySelectorAll('input[required], textarea[required]');

        inputs.forEach(input => {
            input.addEventListener('blur', () => {
                this.validateField(input);
            });

            input.addEventListener('input', () => {
                this.clearFieldError(input);
            });
        });
    }

    validateField(field) {
        const isValid = field.checkValidity();

        if (!isValid && field.value) {
            this.showFieldError(field, 'Por favor, completa este campo correctamente.');
        } else {
            this.clearFieldError(field);
        }

        return isValid;
    }

    showFieldError(field, message) {
        this.clearFieldError(field);

        const errorElement = document.createElement('div');
        errorElement.className = 'field-error';
        errorElement.textContent = message;
        errorElement.style.cssText = `
            color: #ff4757;
            font-size: 0.8rem;
            margin-top: 0.5rem;
            animation: fadeInUp 0.3s ease;
        `;

        field.parentNode.appendChild(errorElement);
        field.setAttribute('aria-invalid', 'true');
    }

    clearFieldError(field) {
        const existingError = field.parentNode.querySelector('.field-error');
        if (existingError) {
            existingError.remove();
        }
        field.removeAttribute('aria-invalid');
    }

    handleInputFocus(e) {
        const input = e.target;
        const formGroup = input.closest('.form-group');

        if (formGroup) {
            formGroup.classList.add('focused');
        }
    }

    handleInputBlur(e) {
        const input = e.target;
        const formGroup = input.closest('.form-group');

        if (formGroup) {
            formGroup.classList.remove('focused');
        }
    }

    async handleFormSubmit(e) {
        e.preventDefault();

        if (!this.validateForm()) {
            this.shakeForm();
            return;
        }

        this.setLoadingState(true);

        try {
            const formData = new FormData(this.form);

            const response = await fetch(this.form.action, {
                method: 'POST',
                body: new URLSearchParams(formData),
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                }
            });

            window.location.href = this.form.action.split('/guardar')[0];

        } catch (error) {
            console.error('Error:', error);
            this.showErrorState('Error al enviar el mensaje. Por favor, intenta nuevamente.');
            this.setLoadingState(false);
        }
    }

    validateForm() {
        const requiredFields = this.form.querySelectorAll('[required]');
        let isValid = true;

        requiredFields.forEach(field => {
            if (!this.validateField(field)) {
                isValid = false;
            }
        });

        return isValid;
    }

    async submitForm() {
        // Simulate API call - reemplaza esto con tu llamada real
        return new Promise((resolve, reject) => {
            setTimeout(() => {
                // Simular éxito la mayoría de las veces
                Math.random() > 0.1 ? resolve() : reject();
            }, 2000);
        });
    }

    setLoadingState(loading) {
        if (loading) {
            this.submitButton.classList.add('loading');
            this.submitButton.disabled = true;
            this.submitButton.innerHTML = 'Enviando...';
        } else {
            this.submitButton.classList.remove('loading');
            this.submitButton.disabled = false;
            this.submitButton.innerHTML = 'Enviar Mensaje';
        }
    }

    showSuccessState() {
        let successMessage = document.querySelector('.message-success');
        if (!successMessage) {
            successMessage = document.createElement('div');
            successMessage.className = 'message-success';
            this.form.appendChild(successMessage);
        }

        successMessage.textContent = '¡Gracias por tu mensaje! Nos pondremos en contacto contigo pronto.';
        successMessage.style.display = 'block';

        setTimeout(() => {
            successMessage.style.display = 'none';
        }, 5000);
    }

    showErrorState(message) {
        const errorElement = document.createElement('div');
        errorElement.className = 'message-error';
        errorElement.textContent = message;
        errorElement.style.cssText = `
            background: rgba(255, 71, 87, 0.1);
            border: 1px solid rgba(255, 71, 87, 0.3);
            color: #ff4757;
            padding: 1rem;
            border-radius: 8px;
            text-align: center;
            margin-top: 1rem;
            animation: fadeInUp 0.3s ease;
        `;

        this.form.appendChild(errorElement);

        setTimeout(() => {
            errorElement.remove();
        }, 5000);
    }

    shakeForm() {
        this.form.style.animation = 'shake 0.5s ease-in-out';
        setTimeout(() => {
            this.form.style.animation = '';
        }, 500);
    }

    resetLabels() {
        const labels = document.querySelectorAll('.form-group label');
        labels.forEach(label => {
            label.classList.remove('floating');
        });
    }
}

// Additional CSS for animations (inject into page)
const contactStyles = `
@keyframes shake {
    0%, 100% { transform: translateX(0); }
    25% { transform: translateX(-10px); }
    75% { transform: translateX(10px); }
}

@keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(10px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.form-group.focused label {
    color: var(--primary-color) !important;
    transform: translateY(-50%) scale(0.9) !important;
}

.form-group label.floating {
    top: 0 !important;
    left: 1rem !important;
    font-size: 0.8rem !important;
    color: var(--primary-color) !important;
    background: var(--container-color) !important;
    transform: translateY(-50%) !important;
}

/* Asegurar scroll suave */
html {
    scroll-behavior: smooth;
}

/* Prevenir problemas de overflow */
body {
    overflow-x: hidden;
}
`;

// Initialize when DOM is ready
document.addEventListener('DOMContentLoaded', function () {
    // Inject additional styles
    const styleSheet = document.createElement('style');
    styleSheet.textContent = contactStyles;
    document.head.appendChild(styleSheet);

    // Initialize form animations
    new ContactFormAnimations();

    // Parallax effect mejorado - solo si el contenido es lo suficientemente alto
    const videoBg = document.querySelector('.contact-video-bg video');
    if (videoBg) {
        let lastScrollY = window.scrollY;

        const handleScroll = () => {
            const scrolled = window.scrollY;
            // Solo aplicar parallax si el scroll es significativo
            if (Math.abs(scrolled - lastScrollY) > 1) {
                const rate = scrolled * -0.3;
                videoBg.style.transform = `translateY(${rate}px) scale(1.05)`;
                lastScrollY = scrolled;
            }

            requestAnimationFrame(handleScroll);
        };

        // Usar requestAnimationFrame para mejor performance
        requestAnimationFrame(handleScroll);
    }
});