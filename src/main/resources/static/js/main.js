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






// Animaciones específicas para la sección HOME
function initHomeAnimations() {
    // Efecto de parallax sutil en el video
    const homeSection = document.querySelector('.home');
    const video = document.querySelector('.home__video');
    
    if (homeSection && video) {
        homeSection.addEventListener('mousemove', (e) => {
            const { left, top, width, height } = homeSection.getBoundingClientRect();
            const x = (e.clientX - left) / width - 0.5;
            const y = (e.clientY - top) / height - 0.5;
            
            video.style.transform = `translate(${x * 20}px, ${y * 15}px) scale(1.02)`;
        });
        
        homeSection.addEventListener('mouseleave', () => {
            video.style.transform = 'translate(0, 0) scale(1)';
        });
    }
    
    // Efecto de escritura para el título (opcional)
    const titleLines = document.querySelectorAll('.title-line');
    titleLines.forEach((line, index) => {
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

// Inicializar cuando el DOM esté listo
document.addEventListener('DOMContentLoaded', initHomeAnimations);