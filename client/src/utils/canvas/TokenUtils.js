import CanvasImgNames from '../../constants/canvas/ImgNames';

class TokenUtils {

    static moneyToTokens(money) {
        if (money < 0) throw new Error("Money have a negative value");
        const results = { '500': 0, '200': 0, '100': 0, '50': 0, '10': 0, '1': 0 };
        let restMoney = money;
        while (restMoney) {
            for (let k = 0; k < Object.keys(results).length; k++) {
                // if we have more than 5k, give priority to bigger tokens (start loop opposite)
                const key = money > 5000 ? Object.keys(results).reverse()[k] : Object.keys(results)[k];
                if (restMoney - +key >= 0) {
                    restMoney -= +key;
                    results[key] += 1;
                }
            }
        }
        return results;
    }

    static tokensToMoney(tokens) {
        let totalMoney = 0;
        for(let tokenColumn in tokens) {
            totalMoney += tokens[tokenColumn] * +tokenColumn;
        }
        return totalMoney;
    }

    static getCurrentTokenColumnImgName(currentTokenColumnIdx) {
        const tokenNames = [
            CanvasImgNames.TOKEN1,
            CanvasImgNames.TOKEN10,
            CanvasImgNames.TOKEN50,
            CanvasImgNames.TOKEN100,
            CanvasImgNames.TOKEN200,
            CanvasImgNames.TOKEN500
        ];
        return tokenNames[currentTokenColumnIdx];
    }
}

export default TokenUtils;



// ALTERNATIVE 1 -- 
// for (let k = Object.keys(results).length - 1; k >= 0; k--) {
//     const key = Object.keys(results)[k];
//     results[key] = Math.floor(restMoney / +key);
//     restMoney %= +key;
//     if (k < Object.keys(results).length - 1) {
//         const previousKey = Object.keys(results)[k + 1];
//         while (results[key] < results[previousKey]) {
//             results[previousKey] -= 1;
//             restMoney += +previousKey;
//             results[key] += Math.floor(restMoney / +key);
//             restMoney %= +key;
//         }
//     }
// }

// ALTERNATIVE -- MORE ELABORATE BUT NOT SURE IF NECESSARY
// results = { '500': 0, '200': 0, '100': 0, '10': 0, '1': 0 };
// let restMoney = 1532;
// let r = 0;
// do {
//     for (let k = Object.keys(results).length - 1; k >= 0; k--) {
//         const key = Object.keys(results)[k];
//         if (!r) {
//             results[key] = Math.floor(restMoney / +key);
//             restMoney %= +key;
//         }
//         if (k < Object.keys(results).length - 1) {
//             const previousKey = Object.keys(results)[k + 1];
//             console.log(results[key], results[previousKey], 'TokenUtils.js', 'line: ', '17');
//             while (results[key] < results[previousKey]) {
//                 results[previousKey] -= 1;
//                 restMoney += +previousKey;
//                 results[key] += Math.floor(restMoney / +key);
//                 restMoney %= +key;
//                 console.log(results[key], results[previousKey], 'TokenUtils.js', 'line: ', '19');
//             }
//         }
//     }
//     r += 1;
// } while ((results["1"] < results["10"] && (results["1"] < 3 || results["10"] < 3)) || 
//         (results["10"] < results["100"] && (results["10"] < 3 ||results["100"] < 3)) || 
//         (results["100"] < results["200"] && (results["100"] < 3 ||results["200"] < 3)) || 
//         (results["200"] < results["500"] && (results["200"] < 3 ||results["500"] < 3)));

// results