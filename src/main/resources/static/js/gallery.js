// GALLERY HERO ANIMATIONS - En archivo separado
class GalleryHeroAnimations {
    constructor() {
        this.galleryHero = document.querySelector('.gallery-hero');
        this.init();
    }

    init() {
        this.setupVideoEffects();
        this.setupScrollAnimations();
    }

    setupVideoEffects() {
        const video = this.galleryHero?.querySelector('.gallery-hero__video');
        if (!video) return;

        // Efecto parallax suave en scroll
        window.addEventListener('scroll', () => {
            const scrolled = window.pageYOffset;
            const rate = scrolled * -0.3;
            video.style.transform = `translateY(${rate}px) scale(1.05)`;
        });

        // Efecto de interacción con mouse
        this.galleryHero.addEventListener('mousemove', (e) => {
            const { left, top, width, height } = this.galleryHero.getBoundingClientRect();
            const x = (e.clientX - left) / width - 0.5;
            const y = (e.clientY - top) / height - 0.5;

            video.style.transform = `translate(${x * 10}px, ${y * 8}px) scale(1.02)`;
        });

        this.galleryHero.addEventListener('mouseleave', () => {
            video.style.transform = 'translate(0, 0) scale(1)';
        });
    }

    setupScrollAnimations() {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.classList.add('hero-visible');
                }
            });
        }, { threshold: 0.3 });

        if (this.galleryHero) {
            observer.observe(this.galleryHero);
        }
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', () => {
    new GalleryHeroAnimations();
});