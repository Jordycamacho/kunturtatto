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

class GalleryFiltersAnimations {
    constructor() {
        this.filtersSection = document.querySelector('.gallery-filters');
        this.filterGroups = document.querySelectorAll('.gallery-filters__group');
        this.filterItems = document.querySelectorAll('.gallery-filters__item');
        this.init();
    }

    init() {
        this.setupScrollAnimations();
        this.setupResponsiveBehavior();
        this.animateOnLoad(); // Animar inmediatamente si ya está visible
    }

    setupScrollAnimations() {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    this.animateFilters();
                    observer.unobserve(entry.target); // Solo animar una vez
                }
            });
        }, {
            threshold: 0.1,
            rootMargin: '0px 0px -50px 0px'
        });

        if (this.filtersSection) {
            observer.observe(this.filtersSection);
        }
    }

    animateOnLoad() {
        // Si la sección ya está visible al cargar, animar inmediatamente
        if (this.filtersSection && this.isElementInViewport(this.filtersSection)) {
            this.animateFilters();
        }
    }

    isElementInViewport(el) {
        const rect = el.getBoundingClientRect();
        return (
            rect.top >= 0 &&
            rect.left >= 0 &&
            rect.bottom <= (window.innerHeight || document.documentElement.clientHeight) &&
            rect.right <= (window.innerWidth || document.documentElement.clientWidth)
        );
    }

    animateFilters() {
        // Animar grupos
        this.filterGroups.forEach((group, index) => {
            setTimeout(() => {
                group.classList.add('animate');
            }, index * 200);
        });

        // Animar items con stagger
        this.filterItems.forEach((item, index) => {
            setTimeout(() => {
                item.classList.add('animate');
            }, 300 + (index * 60));
        });
    }

    setupResponsiveBehavior() {
        this.handleScrollableMenus();

        // Re-evaluar en resize
        window.addEventListener('resize', () => {
            this.handleScrollableMenus();
        });
    }

    handleScrollableMenus() {
        const itemsContainers = document.querySelectorAll('.gallery-filters__items');

        itemsContainers.forEach(container => {
            // Remover clase scrollable primero
            container.classList.remove('gallery-filters__items--scrollable');

            // Aplicar scroll horizontal solo en móvil si hay muchos elementos
            if (window.innerWidth <= 768 && container.children.length > 3) {
                container.classList.add('gallery-filters__items--scrollable');
            }
        });
    }
}

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', function () {
    // Pequeño delay para asegurar que el DOM esté completamente listo
    setTimeout(() => {
        new GalleryFiltersAnimations();
    }, 100);
});

// También inicializar cuando la página termine de cargar
window.addEventListener('load', function () {
    new GalleryFiltersAnimations();
});