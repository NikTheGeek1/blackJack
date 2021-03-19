
export const getFontSize = () => {
    return +window.getComputedStyle(document.getElementsByTagName('body')[0]).fontSize.replace('px', '');
};