package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class ChessClient {
    private static boolean signedIn = false;
    private static PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    UserData user = null;
    GameData game = null;
    AuthData auth = null;



    public static void main(String[] args) {
        out.println("♕ Welcome to 240 Chess!\n\tType \"help\" to get started.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            result = scanner.nextLine();
            String[] splitResult = result.split("\\s+");
            switch(splitResult[0]) {
                case "help" -> help();
                case "register" -> register(splitResult);
                case "login" -> login(splitResult);
                case "create" -> create(splitResult);
                case "join" -> join(splitResult);
                case "list" -> list();
                case "observe" -> observe(splitResult);
                case "logout" -> logout();
                case "quit" -> {
                    if (signedIn) logout();
                    break;
                }
                default -> {
                    setTextColor("Red");
                    out.print("\t" + result + " is an invalid input. Type \"help\" for a list of valid inputs.");
                }
            }
        }
        setTextColor("White");
        setTextStyle("Bold");
        out.println("\nThanks for playing 240 Chess! ♕");
    }






    private static void help() {
        if (!signedIn) {
            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\t" + "register <USERNAME> <PASSWORD> <EMAIL>");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - Create an account");

            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\n\t" + "login <USERNAME> <PASSWORD>");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - Login to an existing account");

            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\n\t" + "quit");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - quit the game");

            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\n\t" + "help");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - display help information");
        }
        else {
            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\t" + "Create <NAME>");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - Create a game");

            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\n\t" + "list");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - list all games");

            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\n\t" + "join <ID> [ WHITE | BLACK | <NONE> ]");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - join a game");

            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\n\t" + "observe <ID>");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - observe a game");

            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\n\t" + "logout");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - logout");

            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\n\t" + "quit");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - quit the game");

            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\n\t" + "help");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - display help information");
        }
    }
    private static void register(String[] arguments) {
        if (signedIn) {
            invalidCommand();
            return;
        }
        if (arguments.length != 4) {
            invalidArguments();
            return;
        }
        String username = arguments[1];
        String password = arguments[2];
        String email = arguments[3];
        setTextColor("Red");
        out.print("UNIMPLEMENTED");
        out.print("\t" + username + EMPTY + password + EMPTY + email);
        signedIn = true;
    }
    private static void login(String[] arguments) {
        if (signedIn) {
            invalidCommand();
            return;
        }
        if (arguments.length != 3) {
            invalidArguments();
            return;
        }
        String username = arguments[1];
        String password = arguments[2];
        setTextColor("Red");
        out.print("UNIMPLEMENTED");
        out.print("\t" + username + EMPTY + password);
        signedIn = true;
    }
    private static void logout() {
        if (!signedIn) {
            invalidCommand();
            return;
        }
        setTextColor("Red");
        out.print("UNIMPLEMENTED");
        signedIn = false;
    }
    private static void create(String[] arguments) {
        if (!signedIn) {
            invalidCommand();
            return;
        }
        if (arguments.length != 2) {
            invalidArguments();
            return;
        }
        String gameName = arguments[1];
        setTextColor("Red");
        out.print("UNIMPLEMENTED");
        out.print("\t" + gameName);
    }
    private static void join(String[] arguments) {
        if (!signedIn) {
            invalidCommand();
            return;
        }
        if (arguments.length != 3 && arguments.length != 2) {
            invalidArguments();
            return;
        }
        String gameId = arguments[1];
        String color = arguments.length == 3 ? arguments[2] : "None";
        if (!(Objects.equals(color, "BLACK")
                || Objects.equals(color, "WHITE")
                || Objects.equals(color, "None"))) {
            invalidArguments();
            return;
        }
        setTextColor("Red");
        out.print("UNIMPLEMENTED");
        out.print("\t" + gameId + EMPTY + color);
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        out.println();
        printBoard("WHITE", board);
        printBoard("BLACK", board);
    }
    private static void observe(String[] arguments) {
        if (!signedIn) {
            invalidCommand();
            return;
        }
        if (arguments.length != 2) {
            invalidArguments();
            return;
        }
        String gameId = arguments[1];
        setTextColor("Red");
        out.print("UNIMPLEMENTED");
        out.print("\t" + gameId);
        ChessBoard board = new ChessBoard();
        board.resetBoard();

        out.println();
        printBoard("WHITE", board);
        printBoard("BLACK", board);
    }
    private static void list() {
        if (!signedIn) {
            invalidCommand();
            return;
        }
        setTextColor("Red");
        out.print("UNIMPLEMENTED");
    }




    private static void printBoard(String color, ChessBoard board) {

        boolean isWhite = color.equals("WHITE");
        out.println();

        setTextColor("White");
        String output = isWhite ? "White player pov" : "Black player pov";
        out.println(output);

        printTop(isWhite);

        boolean whiteSquare = true;
        for (int row = 0; row < 8; row++) {
            whiteSquare = row % 2 == 0;
            if (isWhite) row = 8 - row;
            else row = row + 1;

            setBackgroundColor("Blue");
            setTextColor("Black");

            out.print(" " + (row) + "\u2001");

            for (int col = 0; col < 8; col++) {
                if (isWhite) col = 7 - col;

                if (whiteSquare) {
                    setBackgroundColor("White");
                    whiteSquare = false;
                }
                else {
                    setBackgroundColor("Dark Grey");
                    whiteSquare = true;
                }

                ChessPiece currentPiece = board.getPiece(new ChessPosition(row, col + 1));
                if (currentPiece == null) {
                    out.print(EMPTY);
                    if (isWhite) col = 7 - col;
                    continue;
                }
                ChessPiece.PieceType type = currentPiece.getPieceType();
                ChessGame.TeamColor teamColor = currentPiece.getTeamColor();
                char pieceChar = switch (type) {
                    case KING -> 'K';
                    case QUEEN -> 'Q';
                    case BISHOP -> 'B';
                    case KNIGHT -> 'N';
                    case ROOK -> 'R';
                    case PAWN -> 'P';
                };

                if (teamColor == ChessGame.TeamColor.WHITE) setTextColor("Blue");
                else setTextColor("Red");
                out.print(" " + (pieceChar) + "\u2001");
                if (isWhite) col = 7 - col;
            }

            setBackgroundColor("Blue");
            setTextColor("Black");
            out.print(" " + (row) + "\u2001");
            setBackgroundColor("Black");
            out.print("\n");

            if (isWhite) row = 8 - row;
            else row = row - 1;
        }

        printTop(isWhite);
    }
    private static void printTop(boolean isWhite) {
        char[] alpha = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};

        setBackgroundColor("Blue");
        setTextColor("Black");
        out.print(EMPTY);
        for (int i = 0; i < 8; i++) {
            char outputChar = isWhite ? alpha[7 - i] : alpha[i];
            out.print(" " + outputChar + "\u2001");
        }
        out.print(EMPTY);
        setBackgroundColor("None");
        out.print("\n");
    }
    private static void printPrompt() {
        out.print(ERASE_LINE);
        String status = signedIn ? "LOGGED_IN" : "LOGGED_OUT";
        setTextColor("White");
        out.print(RESET_TEXT_ITALIC);
        setTextStyle("Bold");
        String output = "\n" + "[" + status + "]" + EMPTY + ">>>" + EMPTY;
//        ArrayList<String> colors = new ArrayList<String>();
//        colors.add("White");
//        colors.add("Red");
//        colors.add("Yellow");
//        colors.add("Green");
//        colors.add("Blue");
//        colors.add("Magenta");
//        for (int i = 0; i < output.length(); i++) {
//            setTextColor(colors.get(i%colors.size()));
//            out.print(output.charAt(i));
//        }
        out.print(output);
    }
    private static void invalidCommand() {
        setTextColor("Red");
        out.print("\tInvalid Command!");
        setTextColor("Yellow");
        out.print("\n\tType \"help\" for available commands");
    }
    private static void invalidArguments() {
        setTextColor("Red");
        out.print("\tInvalid arguments!");
        setTextColor("Yellow");
        out.print("\n\tType \"help\" for required arguments.");
    }




    private static void setBackgroundColor(String color) {
        String BG = switch (color) {
            case "Black" -> SET_BG_COLOR_BLACK;
            case "White" -> SET_BG_COLOR_WHITE;
            case "Light Grey" -> SET_BG_COLOR_LIGHT_GREY;
            case "Dark Grey" -> SET_BG_COLOR_DARK_GREY;
            case "Blue" -> SET_BG_COLOR_BLUE;
            case "Green" -> SET_BG_COLOR_GREEN;
            case "Dark Green" -> SET_BG_COLOR_DARK_GREEN;
            case "Red" -> SET_BG_COLOR_RED;
            case "Yellow" -> SET_BG_COLOR_YELLOW;
            case "Magenta" -> SET_BG_COLOR_MAGENTA;
            case "None" -> RESET_BG_COLOR;
            default -> throw new IllegalStateException("Unexpected value: " + color);
        };
        out.print(BG);
    }
    private static void setTextColor(String color) {
        String text = switch (color) {
            case "Black" -> SET_TEXT_COLOR_BLACK;
            case "White" -> SET_TEXT_COLOR_WHITE;
            case "Light Grey" -> SET_TEXT_COLOR_LIGHT_GREY;
            case "Dark Grey" -> SET_TEXT_COLOR_DARK_GREY;
            case "Blue" -> SET_TEXT_COLOR_BLUE;
            case "Green" -> SET_TEXT_COLOR_GREEN;
            case "Red" -> SET_TEXT_COLOR_RED;
            case "Yellow" -> SET_TEXT_COLOR_YELLOW;
            case "Magenta" -> SET_TEXT_COLOR_MAGENTA;
            default -> throw new IllegalStateException("Unexpected value: " + color);
        };
        out.print(text);
    }
    private static void setTextStyle(String style) {
        String s = switch (style) {
            case "Bold" -> SET_TEXT_BOLD;
            case "Italic" -> SET_TEXT_ITALIC;
            case "Blinking" -> SET_TEXT_BLINKING;
            case "Faint" -> SET_TEXT_FAINT;
            case "Underline" -> SET_TEXT_UNDERLINE;
            default -> throw new IllegalStateException("Unexpected value: " + style);
        };
        out.print(RESET_TEXT_ITALIC);
        out.print(RESET_TEXT_BOLD_FAINT);
        out.print(RESET_TEXT_UNDERLINE);
        out.print(s);
    }
}
