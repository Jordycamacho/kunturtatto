// GALLERY HERO ANIMATIONS
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

document.addEventListener('DOMContentLoaded', function () {
    setTimeout(() => {
        new GalleryFiltersAnimations();
    }, 100);
});

window.addEventListener('load', function () {
    new GalleryFiltersAnimations();
});

function revealGalleryImages(root) {
    (root || document).querySelectorAll('.design-image').forEach((img) => {
        const mark = () => {
            img.classList.add('is-shown');
            img.closest('.image')?.classList.add('is-ready');
        };
        if (img.complete && img.naturalWidth > 0) {
            mark();
        } else {
            img.addEventListener('load', mark, { once: true });
            img.addEventListener('error', mark, { once: true });
        }
    });
}

async function swapGallery(url, push) {
    const stage = document.getElementById('gallery-live');
    if (!stage || stage.classList.contains('is-swapping')) {
        return;
    }
    stage.classList.add('is-swapping');
    try {
        const response = await fetch(url, { headers: { 'X-Gallery': '1' } });
        if (!response.ok) {
            throw new Error('gallery');
        }
        const html = await response.text();
        const doc = new DOMParser().parseFromString(html, 'text/html');
        const next = doc.getElementById('gallery-live');
        if (!next) {
            throw new Error('fragment');
        }
        stage.innerHTML = next.innerHTML;
        const nextTitle = doc.querySelector('.gallery-hero__title');
        const title = document.querySelector('.gallery-hero__title');
        if (nextTitle && title) {
            title.textContent = nextTitle.textContent;
        }
        if (push) {
            history.pushState({}, '', url);
        }
        revealGalleryImages(stage);
        const filters = stage.querySelector('.gallery-filters');
        if (filters && filters.getBoundingClientRect().top < 0) {
            filters.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    } catch (error) {
        window.location.href = url;
    } finally {
        stage.classList.remove('is-swapping');
    }
}

document.addEventListener('click', (event) => {
    const link = event.target.closest('#gallery-live a');
    if (!link || event.metaKey || event.ctrlKey || event.shiftKey || event.altKey || event.button !== 0) {
        return;
    }
    const url = new URL(link.href, window.location.origin);
    if (!url.pathname.includes('/Muthabara/dise')) {
        return;
    }
    event.preventDefault();
    swapGallery(url.toString(), true);
});

window.addEventListener('popstate', () => {
    if (document.getElementById('gallery-live')) {
        swapGallery(window.location.href, false);
    }
});

document.addEventListener('DOMContentLoaded', () => revealGalleryImages(document));