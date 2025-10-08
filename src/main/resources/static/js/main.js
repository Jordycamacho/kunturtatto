// =============== NAVIGATION ===============
class Navigation {
    constructor() {
        this.navMenu = document.getElementById('nav-menu');
        this.navToggle = document.getElementById('nav-toggle');
        this.navClose = document.getElementById('nav-close');
        this.navLinks = document.querySelectorAll('.nav__link');
        this.header = document.getElementById('header');

        this.init();
    }

    init() {
        this.setupMobileMenu();
        this.setupScrollEffects();
    }

    setupMobileMenu() {
        if (this.navToggle && this.navMenu) {
            this.navToggle.addEventListener('click', () => {
                this.navMenu.classList.add('show-menu');
                document.body.style.overflow = 'hidden';
            });
        }

        if (this.navClose && this.navMenu) {
            this.navClose.addEventListener('click', () => {
                this.navMenu.classList.remove('show-menu');
                document.body.style.overflow = '';
            });
        }

        this.navLinks.forEach(link => {
            link.addEventListener('click', () => {
                if (this.navMenu) {
                    this.navMenu.classList.remove('show-menu');
                    document.body.style.overflow = '';
                }
            });
        });
    }

    setupScrollEffects() {
        window.addEventListener('scroll', () => {
            if (this.header) {
                window.scrollY >= 50
                    ? this.header.classList.add('blur-header')
                    : this.header.classList.remove('blur-header');
            }
        });

        const sections = document.querySelectorAll('section[id]');

        window.addEventListener('scroll', () => {
            const scrollY = window.pageYOffset;

            sections.forEach(current => {
                const sectionHeight = current.offsetHeight;
                const sectionTop = current.offsetTop - 100;
                const sectionId = current.getAttribute('id');
                const navLink = document.querySelector(`.nav__link[href*="${sectionId}"]`);

                if (navLink) {
                    if (scrollY > sectionTop && scrollY <= sectionTop + sectionHeight) {
                        navLink.classList.add('active');
                    } else {
                        navLink.classList.remove('active');
                    }
                }
            });
        });

        const scrollUp = document.getElementById('scroll-up');
        if (scrollUp) {
            window.addEventListener('scroll', () => {
                window.scrollY >= 350
                    ? scrollUp.classList.add('show-scroll')
                    : scrollUp.classList.remove('show-scroll');
            });
        }
    }
}

// =============== HOME ANIMATIONS ===============
class HomeAnimations {
    constructor() {
        this.homeSection = document.querySelector('.home');
        this.video = document.querySelector('.home__video');
        this.titleLines = document.querySelectorAll('.title-line');

        this.init();
    }

    init() {
        this.setupVideoParallax();
        this.setupTitleAnimation();
    }

    setupVideoParallax() {
        if (this.homeSection && this.video) {
            this.homeSection.addEventListener('mousemove', (e) => {
                const { left, top, width, height } = this.homeSection.getBoundingClientRect();
                const x = (e.clientX - left) / width - 0.5;
                const y = (e.clientY - top) / height - 0.5;

                this.video.style.transform = `translate(${x * 20}px, ${y * 15}px) scale(1.02)`;
            });

            this.homeSection.addEventListener('mouseleave', () => {
                this.video.style.transform = 'translate(0, 0) scale(1)';
            });
        }
    }

    setupTitleAnimation() {

        this.titleLines.forEach((line, index) => {
            const text = line.textContent;
            line.textContent = '';
            let i = 0;

            setTimeout(() => {
                const typeWriter = setInterval(() => {
                    if (i < text.length) {
                        line.textContent += text.charAt(i);
                        i++;
                    } else {
                        clearInterval(typeWriter);
                    }
                }, 100);
            }, 1000 + (index * 200));
        });
    }
}

// =============== GALLERY ===============
class Gallery {
    constructor() {
        this.filterButtons = document.querySelectorAll('.filter-btn');
        this.galleryItems = document.querySelectorAll('.gallery__item');
        this.init();
    }

    init() {
        if (this.filterButtons.length > 0 && this.galleryItems.length > 0) {
            this.setupFiltering();
            this.setupAnimations();
            this.setupImageLoading();
        }
    }

    setupFiltering() {
        this.filterButtons.forEach(button => {
            button.addEventListener('click', () => {
                // Remove active class from all buttons
                this.filterButtons.forEach(btn => btn.classList.remove('active'));

                // Add active class to clicked button
                button.classList.add('active');

                const filterValue = button.getAttribute('data-filter');
                this.filterItems(filterValue);
            });
        });
    }

    filterItems(filter) {
        this.galleryItems.forEach(item => {
            const itemCategory = item.getAttribute('data-category');
            if (filter === 'all' || itemCategory === filter) {
                item.style.display = 'block';
                setTimeout(() => {
                    item.style.opacity = '1';
                    item.style.transform = 'translateY(0)';
                }, 50);
            } else {
                item.style.opacity = '0';
                item.style.transform = 'translateY(20px)';
                setTimeout(() => {
                    item.style.display = 'none';
                }, 400);
            }
        });
    }

    setupAnimations() {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.animationPlayState = 'running';
                }
            });
        }, { threshold: 0.1 });

        this.galleryItems.forEach(item => {
            observer.observe(item);
        });
    }

    setupImageLoading() {
        const images = document.querySelectorAll('.gallery__image');

        images.forEach(img => {
            img.addEventListener('load', () => {
                img.style.opacity = '1';
            });

            img.addEventListener('error', () => {
                console.warn('Error loading image:', img.src);
            });
        });
    }
}

// =============== CARRUCEL ===============
class DecorativeCarousels {
    constructor() {
        this.carousels = document.querySelectorAll('.carousel');
        this.init();
    }

    init() {
        this.setupScrollControl();
        this.setupHoverEffects();
    }

    setupScrollControl() {
        window.addEventListener('scroll', () => {
            const scrollY = window.scrollY;
            const carouselsSection = document.querySelector('.decorative-carousels');

            if (carouselsSection) {
                const sectionTop = carouselsSection.offsetTop;
                const sectionHeight = carouselsSection.offsetHeight;
                const windowHeight = window.innerHeight;

                if (scrollY > sectionTop - windowHeight && scrollY < sectionTop + sectionHeight) {
                    const progress = (scrollY - (sectionTop - windowHeight)) / (windowHeight + sectionHeight);
                    this.adjustAnimationSpeed(progress);
                }
            }
        });
    }

    adjustAnimationSpeed(progress) {
        const baseSpeed = 40;
        const speedMultiplier = 0.5 + progress * 0.5;

        this.carousels.forEach(carousel => {
            const track = carousel.querySelector('.carousel__track');
            if (track) {
                track.style.animationDuration = `${baseSpeed / speedMultiplier}s`;
            }
        });
    }

    setupHoverEffects() {
        this.carousels.forEach(carousel => {
            carousel.addEventListener('mouseenter', () => {
                carousel.classList.add('carousel--paused');
            });

            carousel.addEventListener('mouseleave', () => {
                carousel.classList.remove('carousel--paused');
            });
        });
    }
}

// =============== DESINGS ===============
class DesignsSection {
    constructor() {
        this.designItems = document.querySelectorAll('.designs__item');
        this.init();
    }

    init() {
        this.setupScrollAnimations();
        this.setupHoverEffects();
    }

    setupScrollAnimations() {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    entry.target.style.animationPlayState = 'running';
                }
            });
        }, { threshold: 0.1 });

        this.designItems.forEach(item => {
            observer.observe(item);
        });
    }

    setupHoverEffects() {
        this.designItems.forEach(item => {
            const image = item.querySelector('.designs__image');

            item.addEventListener('mousemove', (e) => {
                const { left, top, width, height } = item.getBoundingClientRect();
                const x = (e.clientX - left) / width - 0.5;
                const y = (e.clientY - top) / height - 0.5;

                if (image) {
                    image.style.transform = `scale(1.08) translate(${x * 10}px, ${y * 10}px)`;
                }
            });

            item.addEventListener('mouseleave', () => {
                if (image) {
                    image.style.transform = 'scale(1.08) translate(0, 0)';
                }
            });
        });
    }
}

// =============== STORE SECTION ===============
class StoreSection {
    constructor() {
        this.storeSection = document.querySelector('.store');
        this.productCards = document.querySelectorAll('.store__product-card');
        this.init();
    }

    init() {
        this.setupScrollAnimations();
        this.setupHoverEffects();
        this.setupProductCardInteractions();
    }

    setupScrollAnimations() {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    this.animateOnScroll();
                }
            });
        }, { threshold: 0.3 });

        if (this.storeSection) {
            observer.observe(this.storeSection);
        }
    }

    animateOnScroll() {
        console.log('Store section in view');
    }

    setupHoverEffects() {
        const imageContainer = document.querySelector('.store__image-container');

        if (imageContainer) {
            imageContainer.addEventListener('mousemove', (e) => {
                const { left, top, width, height } = imageContainer.getBoundingClientRect();
                const x = (e.clientX - left) / width - 0.5;
                const y = (e.clientY - top) / height - 0.5;

                imageContainer.style.transform = `perspective(1000px) rotateY(${x * 5}deg) rotateX(${y * -5}deg)`;
            });

            imageContainer.addEventListener('mouseleave', () => {
                imageContainer.style.transform = 'perspective(1000px) rotateY(0) rotateX(0)';
            });
        }
    }

    setupProductCardInteractions() {
        this.productCards.forEach(card => {
            card.addEventListener('mouseenter', () => {
                card.style.zIndex = '10';
            });

            card.addEventListener('mouseleave', () => {
                card.style.zIndex = '1';
            });
        });
    }
}

// =============== FOOTER ===============
class FooterAnimations {
    constructor() {
        this.footer = document.querySelector('.footer');
        this.init();
    }

    init() {
        this.setupScrollAnimations();
    }

    setupScrollAnimations() {
        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    this.animateElements();
                }
            });
        }, { threshold: 0.3 });

        if (this.footer) {
            observer.observe(this.footer);
        }
    }

    animateElements() {
        const elements = [
            ...document.querySelectorAll('.footer__logo'),
            ...document.querySelectorAll('.footer__description'),
            ...document.querySelectorAll('.footer__title'),
            ...document.querySelectorAll('.footer__link'),
            ...document.querySelectorAll('.footer__social-link'),
            ...document.querySelectorAll('.footer__copy')
        ];

        elements.forEach((element, index) => {
            setTimeout(() => {
                element.classList.add('animate');
            }, index * 100);
        });
    }
}
// =============== INITIALIZE EVERYTHING ===============
document.addEventListener('DOMContentLoaded', () => {
    new Navigation();

    if (document.querySelector('.home')) {
        new HomeAnimations();
    }

    if (document.querySelector('.gallery')) {
        new Gallery();
    }

    if (document.querySelector('.decorative-carousels')) {
        new DecorativeCarousels();
    }

    if (document.querySelector('.designs')) {
        new DesignsSection();
    }

    if (document.querySelector('.store')) {
        new StoreSection();
    }

    if (document.querySelector('.footer')) {
        new FooterAnimations();
    }
});

// =============== SCROLL TO TOP ===============
const scrollUp = document.getElementById('scroll-up');
if (scrollUp) {
    scrollUp.addEventListener('click', () => {
        window.scrollTo({
            top: 0,
            behavior: 'smooth'
        });
    });
}

// =============== SMOOTH SCROLL FOR ANCHOR LINKS ===============
document.querySelectorAll('a[href^="#"]').forEach(anchor => {
    anchor.addEventListener('click', function (e) {
        e.preventDefault();
        const target = document.querySelector(this.getAttribute('href'));
        if (target) {
            target.scrollIntoView({
                behavior: 'smooth',
                block: 'start'
            });
        }
    });
});