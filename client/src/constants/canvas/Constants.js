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
        { x: 85, y: 25, angle: 40 }, 
        { x: 110, y: 45, angle: 50 },
        { x: 140, y: 85, angle: 60 },
        { x: 170, y: 120, angle: 70 },
        { x: 200, y: 160, angle: 80 },
        { x: 230, y: 200, angle: 90 },
        { x: 260, y: 240, angle: 100 }, 
        { x: 290, y: 280, angle: 110 },
        { x: 320, y: 320, angle: 120 }
    ];

    static ENLARGED_CARD_NUM_OFFSETS = [
        { x: -50, y: -50, angle: -20 },
        { x: -10, y: -60, angle: -10 },
        { x: 30, y: -70, angle: 0 },
        { x: 70, y: -80, angle: 10 }, 
        { x: 110, y: -90, angle: 20 },
        { x: 150, y: -100, angle: 30 },
        { x: 190, y: -110, angle: 40 },
        { x: 270, y: -120, angle: 50 },
        { x: 310, y: -130, angle: 60 },
        { x: 350, y: -140, angle: 70 }, 
        { x: 390, y: -150, angle: 80 },
        { x: 430, y: -160, angle: 90 },
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
    static CARD_INTERVAL = 16;

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

    static START_BUTTON_COORDS = { x: 400, y: 400 };
    static BET_BUTTON_COORDS = { x: 400, y: 400 };
    static DRAW_BUTTON_COORDS = { x: 500, y: 400 };
    static STICK_BUTTON_COORDS = { x: 300, y: 400 };

    static START_BUTTON_SIZE = { width: 300, height: 50 };
    static BET_BUTTON_SIZE = { width: 300, height: 50 };
    static DRAW_BUTTON_SIZE = { width: 250, height: 50 };
    static STICK_BUTTON_SIZE = { width: 250, height: 50 };

    static NAMES_COORDS = [
        { x: 560, y: 70, angle: 0 }, // dealer
        { x: 10, y: 310, angle: 70 },
        { x: 130, y: 485, angle: 45 },
        { x: 340, y: 590, angle: 13 },
        { x: 590, y: 615, angle: -13 },
        { x: 800, y: 537, angle: -40 },
        { x: 910, y: 440, angle: -55 },
    ];


    static VERDICTS_COORDS = [
        { x: null, y: null, angle: null }, // dealer doesn't have a verdict
        { x: 10, y: 310, angle: 70 },
        { x: 130, y: 485, angle: 45 },
        { x: 340, y: 590, angle: 13 },
        { x: 590, y: 615, angle: -13 },
        { x: 800, y: 537, angle: -40 },
        { x: 910, y: 440, angle: -55 },
    ]

    static BET_ARROW_COORDS = { x: 70, y: 625, angle: -90 };
    static BET_MESSAGE_OFFSETS = { x: -30, y: -20, angle: -40 };
    static BUSTED_MSG_COORDS = { x: 400, y: 380 };
    static BJ_MSG_COORDS = { x: 400, y: 380 };
    static VERDICT_COORDS = { x: 400, y: 380 };
    static DEALER_WAITING_MSG_COORDS = { x: 350, y: 350 };

    static MESSAGES_FONT = "48px serif";
    static MESSAGES_COLOUR = "";
    static PLAYER_NAMES_FONT = "30px serif";
    static ALL_VERDICTS_FONT = "30px serif";
    static TIME_TO_BET_FONT = "28px serif";
    static BUSTED_FONT = "48px serif";
    static BJ_FONT = "48px serif";
    static VERDICT_FONT = "48px serif";

    static THIS_PLAYER_ENLARGED_CARDS = [
        {x: 630, y: 580, angle: -20}, // card 1;
        {x: 670, y: 570, angle: -10},
        {x: 710, y: 560, angle: 0},
        {x: 750, y: 550, angle: 10},
        {x: 790, y: 540, angle: 20},
        {x: 830, y: 530, angle: 30},
        {x: 870, y: 520, angle: 40},
        {x: 910, y: 510, angle: 50},
        {x: 950, y: 500, angle: 60},
        {x: 990, y: 490, angle: 70},
        {x: 1030, y: 480, angle: 80},
        {x: 1070, y: 470, angle: 90},
    ]
}


export default CanvasConstants;