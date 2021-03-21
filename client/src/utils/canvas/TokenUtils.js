import CanvasImgNames from '../../constants/CanvasImgNames';

class TokenUtils {

    static moneyToTokens(money) {
        const results = {'500': 0,'200': 0,'10': 0,'1': 0};
        let restMoney = money;
        for (let k = Object.keys(results).length - 1; k >= 0; k--) {
            const key = Object.keys(results)[k];
            results[key] = Math.floor(restMoney / +key);
            restMoney %= +key;
            if (k < Object.keys(results).length - 1) {
                const previousKey = Object.keys(results)[k + 1];
                while (results[key] < results[previousKey]) {
                    results[previousKey] -= 1;
                    restMoney += +previousKey;
                    results[key] += Math.floor(restMoney / +key);
                    restMoney %= +key;
                }
            }
        }
        return results;
    }

    static getCurrentTokenColumnImgName(currentTokenColumnIdx) {
        const tokenNames = [
            CanvasImgNames.TOKEN1,
            CanvasImgNames.TOKEN10,
            CanvasImgNames.TOKEN200,
            CanvasImgNames.TOKEN500
        ];
        return tokenNames[currentTokenColumnIdx];
    }
}

export default TokenUtils;