// =============== SHOW MENU ===============
const navMenu = document.getElementById('nav-menu'),
    navToggle = document.getElementById('nav-toggle'),
    navClose = document.getElementById('nav-close');

if (navToggle && navMenu) {
    navToggle.addEventListener('click', () => {
        navMenu.classList.add('show-menu');
        document.body.style.overflow = 'hidden';
    });
}

if (navClose && navMenu) {
    navClose.addEventListener('click', () => {
        navMenu.classList.remove('show-menu');
        document.body.style.overflow = '';
    });
}

// =============== REMOVE MENU MOBILE ===============
const navLinks = document.querySelectorAll('.nav__link');

const linkAction = () => {
    const navMenu = document.getElementById('nav-menu');
    if (navMenu) {
        navMenu.classList.remove('show-menu');
        document.body.style.overflow = '';
    }
}

navLinks.forEach(n => n.addEventListener('click', linkAction));

// =============== ADD BLUR TO HEADER ===============
const blurHeader = () => {
    const header = document.getElementById('header');
    if (header) {
        this.scrollY >= 50 ? header.classList.add('blur-header')
            : header.classList.remove('blur-header');
    }
}
window.addEventListener('scroll', blurHeader);

// =============== ACTIVE LINK ===============
const sections = document.querySelectorAll('section[id]');

const scrollActive = () => {
    const scrollY = window.pageYOffset;

    sections.forEach(current => {
        const sectionHeight = current.offsetHeight,
            sectionTop = current.offsetTop - 58,
            sectionId = current.getAttribute('id'),
            sectionsClass = document.querySelector('.nav__menu a[href*=' + sectionId + ']');

        if (sectionsClass) {
            if (scrollY > sectionTop && scrollY <= sectionTop + sectionHeight) {
                sectionsClass.classList.add('active');
            } else {
                sectionsClass.classList.remove('active');
            }
        }
    });
}
window.addEventListener('scroll', scrollActive);

// =============== SHOW SCROLL UP =============== 
const scrollUp = () => {
    const scrollUp = document.getElementById('scroll-up');
    if (scrollUp) {
        this.scrollY >= 350 ? scrollUp.classList.add('show-scroll')
            : scrollUp.classList.remove('show-scroll');
    }
}
window.addEventListener('scroll', scrollUp);