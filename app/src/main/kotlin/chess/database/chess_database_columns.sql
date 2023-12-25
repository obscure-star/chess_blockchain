-- add columns to table
CREATE TABLE CHESS_DATA (
                            ROUND_ID VARCHAR(36) PRIMARY KEY,
                            GAME_ID VARCHAR(36),
                            ROUND INT,
                            BOARD_REPRESENTATION VARCHAR(64),
                            PIECE_COUNT JSON,
                            LEGAL_MOVES JSON,
                            THREATS_AND_ATTACKS JSON,
                            PIECE_ACTIVITY JSON,
                            KING_SAFETY JSON,
                            PAWN_STRUCTURE JSON,
                            MATERIAL_BALANCE JSON,
                            CENTER_CONTROL JSON,
                            PREVIOUS_MOVES JSON,
                            MOVE VARCHAR(36),
                            WHITE_WINS BOOLEAN,
                            BLACK_WINS BOOLEAN,
                            WINNER INT
);

CREATE TABLE CHESS_DATA_NEW AS
SELECT
    ROUND_ID,
    GAME_ID,
    ROUND,
    BOARD_REPRESENTATION,
    PIECE_COUNT,
    LEGAL_MOVES,
    THREATS_AND_ATTACKS,
    PIECE_ACTIVITY,
    KING_SAFETY,
    PAWN_STRUCTURE,
    MATERIAL_BALANCE,
    CENTER_CONTROL,
    PREVIOUS_MOVES,
    MOVE,
    WHITE_WINS,
    BLACK_WINS,
    WINNER,
    COALESCE(LEAD(MOVE) OVER (PARTITION BY GAME_ID ORDER BY ROUND), '') AS NEXT_MOVE
FROM CHESS_DATA;

DROP TABLE CHESS_DATA;

ALTER TABLE CHESS_DATA_NEW
    RENAME TO CHESS_DATA;


