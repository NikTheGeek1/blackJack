import CanvasImgNames from '../../constants/CanvasImgNames';

class RevealedCardUtils {
    static getCardImgName(card) {

        switch (card.rank) {
            case "ACE1":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_ACE_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_ACE_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_ACE_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_ACE_HEARTS; 
                return;
            
                case "ACE11":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_ACE_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_ACE_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_ACE_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_ACE_HEARTS; 
                return;
                
            case "TWO":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_TWO_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_TWO_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_TWO_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_TWO_HEARTS; 
                return;
                
            case "THREE":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_THREE_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_THREE_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_THREE_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_THREE_HEARTS; 
                return;
                
            case "FOUR":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_FOUR_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_FOUR_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_FOUR_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_FOUR_HEARTS; 
                return;
                
            case "FIVE":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_FIVE_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_FIVE_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_FIVE_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_FIVE_HEARTS; 
                return;
                
            case "SIX":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_SIX_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_SIX_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_SIX_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_SIX_HEARTS; 
                return;
                
            case "SEVEN":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_SEVEN_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_SEVEN_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_SEVEN_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_SEVEN_HEARTS; 
                return;
                
            case "EIGHT":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_EIGHT_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_EIGHT_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_EIGHT_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_EIGHT_HEARTS; 
                return;
                
            case "NINE":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_NINE_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_NINE_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_NINE_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_NINE_HEARTS; 
                return;
                
            case "TEN":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_TEN_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_TEN_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_TEN_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_TEN_HEARTS; 
                return;
                
            case "JACK":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_JACK_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_JACK_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_JACK_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_JACK_HEARTS; 
                return;
                
            case "QUEEN":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_QUEEN_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_QUEEN_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_QUEEN_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_QUEEN_HEARTS; 
                return;
                
            case "KING":
                if (card.suit === "SPADES" ) return CanvasImgNames.CARD_KING_SPADES; 
                if (card.suit === "CLUBS")  return CanvasImgNames.CARD_KING_CLUBS; 
                if (card.suit === "DIAMONDS")  return CanvasImgNames.CARD_KING_DIAMONDS; 
                if (card.suit === "HEARTS")  return CanvasImgNames.CARD_KING_HEARTS; 
                return;
                
            default:
                console.log(card, 'RevealedCardUtils.js', 'line: ', '98');
                throw new Error("You shouldn't be here. ");
        }
    }
}


export default RevealedCardUtils;