class CanvasConstants {
    static SCALING_DENOMINATOR = 1350;
    static DEALER_X_OFFSET = 0;
    static DEALER_Y_OFFSET = -250;

    static Ps_1_6_POSITION_OFFSET_X = 420; //players 1 and 6
    static Ps_1_6_POSITION_OFFSET_Y = 0;
    static Ps_2_5_POSITION_OFFSET_X = 300;
    static Ps_2_5_POSITION_OFFSET_Y = 140;
    static Ps_3_4_POSITION_OFFSET_X = 110;
    static Ps_3_4_POSITION_OFFSET_Y = 225;

    static Ps_CARD_OFFSETS = [
        { x: -30, y: 80 }, // dealer
        { x: 0, y: -60 },
        { x: 0, y: -62 },
        { x: 0, y: -67 },
        { x: 0, y: -75 },
        { x: 0, y: -80 },
        { x: -40, y: -85 } // player 6
    ];

    static CARD_NUM_OFFSETS = [
        { x: 0, y: 0, angle: 0 },
        { x: 30, y: 0, angle: 20 },
        { x: 60, y: 10, angle: 30 },
        { x: 85, y: 25, angle: 40 }, //TODO: ADD MORE OFFSETS HERE FOR MORE CARDS. IMPORTANT!
    ];

    static TOKENS_IN_EACH_WRAP = 5;
    static TOKEN_WRAP_MULTIPLIER = 80;
    static TOKEN_NUM_Y_OFFSET = -10;
    static TOKENS_NUM_OFFSETS = [...Array(200).keys()].map(multiplier => {
        return { 
            x: (multiplier % 3), 
            y: Math.floor(multiplier / this.TOKENS_IN_EACH_WRAP) * this.TOKEN_WRAP_MULTIPLIER + (multiplier * this.TOKEN_NUM_Y_OFFSET)
        };
    });

    static BET_TOKEN_NUM_Y_OFFSET = -5;
    static BET_TOKENS_NUM_OFFSETS = [...Array(200).keys()].map(multiplier => {
        return {
            x: (multiplier % 3), 
            y: multiplier * this.BET_TOKEN_NUM_Y_OFFSET
        };
    });

    static DEALING_CARD_INITIAL_COORDS = { x: 500, y: 20 };
    static CARD_INTERVAL = 10;

    static TOKENS_ANIMATION_INITIAL_COORDS = [
        { x: 200, y: 0 },
        { x: 200, y: 0 },
        { x: 200, y: 0 },
        { x: 200, y: 0 },
        { x: 200, y: 0 },
        { x: 200, y: 0 }
    ];

    // final coordinates of tokens. reference point: center of table;
    static TOKEN_COLUMNS_OFFSETS = [
        { x: -100, y: 400 },
        { x: -170, y: 400 },
        { x: -240, y: 400 },
        { x: -310, y: 400 },
        { x: -380, y: 400 },
        { x: -450, y: 400 },
    ];

    static BET_COLUMNS_OFFSETS = [
        { x: 0, y: 0 },
        { x: 20, y: 0 },
        { x: 40, y: 0 },
        { x: 60, y: 0 },
        { x: 80, y: 0 },
        { x: 100, y: 0 },
    ];

}


export default CanvasConstants;