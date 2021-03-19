import remToPxMultiplier from '../constants/pxToRemMultiplier';

export const remToPx = rem => {
    return rem * remToPxMultiplier();
};

export const remToPxString = rem => {
    return remToPx(rem) + 'px';
}

