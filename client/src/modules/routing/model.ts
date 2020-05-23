export const HOME = 'location/HOME'
export const FAQ = 'localtion/FAQ'
export const ABOUT = 'location/ABOUT'
export const DESIGNER = 'location/DESIGNER'

export const URLS = {
    HOME: '/',
    FAQ: '/faq',
    ABOUT: '/about',
    DESIGNER: '/designer'
}

    
const Home = lazy(() => import('../../modules/home/Home'));
const About = lazy(() => import('../../modules/about/About'));
const Faq = lazy(() => import('../../modules/faq/Faq'));
const Page404 = lazy(() => import('../../modules/routing/Page404'));
];
