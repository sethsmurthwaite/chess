package ui;
import chess.*;
import model.*;
import webSocketMessages.serverMessages.ErrorNotification;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.userCommands.*;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static ui.EscapeSequences.*;
public class ChessClient {
    static ChessServerFacade facade;
    private static boolean signedIn = false;
    private static final PrintStream out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
    static Scanner scanner = new Scanner(System.in);
    static UserData user;
    GameData game;
    static GameData[] gameList;
    static AuthData auth;
    static Boolean isPlayer = false;
    boolean isObserver = false;
    static WSClient webSocketClient;
    boolean isPrinting = false;
    ChessGame.TeamColor color;
    boolean gameOver = false;
    HashSet<ChessMove> validMoves = new HashSet<>();
    public static void main(String[] args) {
        ChessClient chessClient = new ChessClient();
        chessClient.run(null);
    }
    public void run(String[] args) {
        facade = new ChessServerFacade(8080);
        try {
            String clientID = this.toString();
            webSocketClient = new WSClient(this, clientID);
        } catch (Exception ignore) {
            System.out.println("Somethign went wrong trying to init wsClient");
            System.out.println(ignore.getMessage());
        }
        for (int i = 0; i < 100; i++) {
            out.print("\n");
        }
        setTextColor("Green");
        out.print("\tWelcome to 240 Chess!\n\tType \"help\" to get started.");
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            result = scanner.nextLine();
            String[] splitResult = result.split("\\s+");
            switch (splitResult[0]) {
                case "help" -> help();
                case "register" -> register(splitResult);
                case "login" -> login(splitResult);
                case "create" -> create(splitResult);
                case "join" -> join(splitResult);
                case "list" -> list();
                case "observe" -> observe(splitResult);
                case "logout" -> logout();
                case "clear" -> {for (int i = 0; i < 100; i++) {out.print("\n");}}
                case "quit" -> {if (signedIn) logout();break;}
                default -> {setTextColor("Red");out.print("\t" + result + " is an invalid input. Type \"help\" for a list of valid inputs.");}
            }
        }
        setTextColor("White");
        setTextStyle("Bold");
        out.println("\n\tThanks for playing 240 Chess!");
    }
    public void gameUI() {
        setTextColor("Green");
        out.print("\tType 'help' for available game commands.");
        String result = "";
        while (!result.equals("leave")) {
            printPrompt();
            result = scanner.nextLine();
            String[] splitResult = result.split("\\s+");
            switch (splitResult[0]) {
                case "help" -> gameHelp();
                case "leave" -> leave();
                case "resign" -> {
                    if (gameOver) invalidCommand();
                    resign();
                }
                case "redraw" -> redraw();
                case "make" -> {
                    if (gameOver) {
                        invalidCommand();
                        break;
                    }
                    if (!splitResult[1].equals("move")) {
                        invalidCommand();
                        break;
                    }
                    makeMove(splitResult[2]);
                }
                case "highlight" -> highlight(splitResult[1]);
                case "clear" -> {
                    for (int i = 0; i < 100; i++) {
                        out.print("\n");
                    }
                }
                default -> invalidCommand();
            }
        }
        isPlayer = false;
    }

    private void gameHelp() {
        if (game != null) {
            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\t" + "redraw");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - Redraws the chess board upon request.");
        }
        setTextColor("Green");
        setTextStyle("Bold");
        out.print("\n\t" + "leave");
        setTextColor("Light Grey");
        setTextStyle("Italic");
        out.print(" - leave the game.");
        if (isPlayer && !gameOver) {
            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\n\t" + "make move");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - Make a move. (ie a2a3)");
            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\n\t" + "resign");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - resign the game.");
            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\n\t" + "highlight");
            setTextColor("Light Grey");
            setTextStyle("Italic");
            out.print(" - highlights legal moves.");
        }
        setTextColor("Green");
        setTextStyle("Bold");
        out.print("\n\t" + "help");
        setTextColor("Light Grey");
        setTextStyle("Italic");
        out.print(" - display available commands.");
    }
    private void leave() {
        int id = game.gameID();
        Leave leave = new Leave(auth.authToken(), id);
        ntfy(webSocketClient.leaveNotification(leave));
        setTextColor("Green");
        out.print("\tYou have left the game.");
        isPlayer = false;
        isObserver = false;
    }
    private void resign() {
        setTextColor("Yellow");
        out.print("\tAre you sure you want to resign? (y/n)  >>>  ");
        String response = scanner.nextLine();
        switch (response.toLowerCase()) {
            case "y" -> {
                gameOver = true;
                Resign resign = new Resign(auth.authToken(), game.gameID());
                ntfy(webSocketClient.resignNotification(resign));
                setTextColor("White");
                out.print("\tYou have resigned. You lose.");
            }
            case "n" -> {
                out.print("\tYou have not resigned.");
            }
            default -> {
                setTextColor("Red");
                out.print("\t" + response + " is not a valid input");
            }
        }
    }
    private void makeMove(String move) {
        ChessMove chessMove = moveFromString(move);
        MakeMove makeMove = new MakeMove(auth.authToken(), chessMove, game.gameID());
        ntfy(webSocketClient.moveNotification(makeMove));
    }
    private void redraw() {
        if (color == null) {
            printBoard("WHITE", game.game().getBoard());
            return;
        }
        if (color.toString().equals("WHITE")) printBoard("WHITE", game.game().getBoard());
        if (color.toString().equals("BLACK")) printBoard("BLACK", game.game().getBoard());
    }
    private void highlight(String move) {
        if (move.length() != 2 || (!Character.isLetter(move.charAt(0)) || !Character.isDigit(move.charAt(1)))) {
            setTextColor("Red");
            out.print("\tInvalid Move");
            return;
        }
        ChessPosition chessPosition = getRowAndColValues(move);
        try {
            validMoves = ChessServerFacade.getValidMoves(auth.authToken(), chessPosition, game.gameID());
            setTextColor("Green");
            out.print("SUCCESS!");
            out.print(validMoves.toString());
            printBoard(color.toString(), game.game().getBoard());
            validMoves.clear();
        } catch (IOException | InterruptedException e) {
            setTextColor("Red");
            out.print("Error while getting validMoves");
            throw new RuntimeException(e);
        }
    }
    private static ChessPosition getRowAndColValues(String move) {
        char[] alpha = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        char colChar = move.charAt(0);
        int col = -1;
        for (int i = 0; i < alpha.length; i++) {
            if (alpha[i] == colChar) {
                col = i + 1;
                break;
            }
        }
        int row = Character.getNumericValue(move.charAt(1));
        return new ChessPosition(row, col);
    }
    private ChessMove moveFromString(String move) {
        if (move.length() != 4 || (!Character.isLetter(move.charAt(0)) || !Character.isDigit(move.charAt(1)) || !Character.isLetter(move.charAt(2)) || !Character.isDigit(move.charAt(3)))) {
            setTextColor("Red");
            out.print("\tInvalid Move");
            return null;
        }
        char[] alpha = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        char colChar = move.charAt(0);
        int col = -1;
        for (int i = 0; i < alpha.length; i++) {
            if (alpha[i] == colChar) {
                col = i + 1;
                break;
            }
        }
        int row = Character.getNumericValue(move.charAt(1));
        ChessPosition currentPosition = new ChessPosition(row, col);
        colChar = move.charAt(2);
        col = -1;
        for (int i = 0; i < alpha.length; i++) {
            if (alpha[i] == colChar) {
                col = i + 1;
                break;
            }
        }
        row = Character.getNumericValue(move.charAt(3));
        ChessPosition targetPosition = new ChessPosition(row, col);
        return new ChessMove(currentPosition, targetPosition);
    }
    private void help() {
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
        } else {
            setTextColor("Green");
            setTextStyle("Bold");
            out.print("\t" + "create <NAME>");
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
            out.print(" - display help information");}}
    private void register(String[] arguments) {
        if (signedIn) {
            invalidCommand();
            return;}
        if (arguments.length != 4) {
            invalidArguments();
            return;}
        String username = arguments[1];
        String password = arguments[2];
        String email = arguments[3];
        user = new UserData(username, password, email);
        try {
            auth = ChessServerFacade.register(user);
        } catch (Error | IOException | InterruptedException e) {
            setTextColor("Red");
            out.print("\t" + e.getMessage());
            user = null;
            return;}
        setTextColor("Green");
        out.print("\tSuccessfully registered user: ");
        setTextColor("White");
        setTextStyle("Bold");
        out.print(user.username());
        out.print(RESET_TEXT_BOLD_FAINT);
        setTextColor("Green");
        out.print("\n\tSuccessfully logged in!\n\tType \"help\" for available commands");
        signedIn = true;}
    private void login(String[] arguments) {
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
        user = new UserData(username, password, "");
        try {
            auth = ChessServerFacade.login(user);
            signedIn = true;
            setTextColor("Green");
            out.print("\tSuccessfully logged in user: ");
            setTextColor("White");
            setTextStyle("Bold");
            out.print(user.username());
            out.print(RESET_TEXT_BOLD_FAINT);
            setTextColor("Green");
            out.print("\n\tType \"help\" for available commands");
        } catch (Error | IOException | InterruptedException e) {
            setTextColor("Red");
            out.print("\tError while logging in.\n\t" + e.getMessage());
        }
    }
    private void logout() {
        if (!signedIn) {
            invalidCommand();
            return;
        }
        try {
            ChessServerFacade.logout(auth);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        setTextColor("Green");
        out.print("\tLogged out user: ");
        setTextColor("White");
        setTextStyle("Bold");
        out.print(user.username());
        out.print(RESET_TEXT_BOLD_FAINT);
        setTextColor("Green");
        out.print("\n\tGoodbye!");
        user = null;
        auth = null;
        signedIn = false;
    }
    private void create(String[] arguments) {
        if (!signedIn) {
            invalidCommand();
            return;
        }
        if (arguments.length != 2) {
            invalidArguments();
            return;
        }
        String gameName = arguments[1];
        GameName name;
        try {
            ChessServerFacade.create(gameName, auth);
        } catch (Error | IOException | InterruptedException e) {
            setTextColor("Red");
            out.print("\tError.\n\t" + e.getMessage());
            return;
        }
        setTextColor("Green");
        out.print("\tSuccessfully created game ");
        setTextColor("White");
        setTextStyle("Bold");
        out.print(gameName);
        out.print(RESET_TEXT_BOLD_FAINT);
        try {
            gameList = getList();
        } catch (Error | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }private void join(String[] arguments) {if (!signedIn) {invalidCommand();return;}if (arguments.length != 3 && arguments.length != 2) {invalidArguments();return;}int gameId;try {gameId = Integer.parseInt(arguments[1]);} catch (NumberFormatException e) {System.err.println("\tERROR: Invalid integer format for gameId: " + arguments[1]);return;}String color = arguments.length == 3 ? arguments[2] : "None";ChessGame.TeamColor playerColor = null;if (color.equalsIgnoreCase("WHITE")) playerColor = ChessGame.TeamColor.WHITE;if (color.equalsIgnoreCase("BLACK")) playerColor = ChessGame.TeamColor.BLACK;if (playerColor == null) {invalidArguments();setTextColor("Red");out.print("\t" + color + " is not a valid color entry.");return;}this.color = playerColor;try {gameList = getList();} catch (Error | IOException | InterruptedException e) {throw new RuntimeException(e);}if (!(0 < gameId && gameId <= gameList.length)) {setTextColor("Red");out.print("\tInvalid Game ID");return;}game = gameList[gameId - 1];try {ChessServerFacade.join(auth, color, game);JoinPlayer joinPlayer = new JoinPlayer(auth.authToken(), playerColor, game.gameID());ntfy(webSocketClient.joinNotification(joinPlayer));ChessBoard board = game.game().getBoard();isPlayer = true;gameUI();} catch (Error | IOException | InterruptedException e) {setTextColor("Red");out.print("\tCannot join game.");}}private void observe(String[] arguments) {if (!signedIn) {invalidCommand();return;}if (arguments.length != 2) {invalidArguments();return;}int gameId = Integer.parseInt(arguments[1]);try {gameList = getList();} catch (Error | IOException | InterruptedException e) {out.print("There was an error while attempting to update the game list");}if (!(0 < gameId && gameId <= gameList.length)) {setTextColor("Red");out.print("\tInvalid Game ID");return;}GameData game = gameList[gameId - 1];try {String color = "None";Observe observe = new Observe(auth.authToken(), game.gameID());ntfy(webSocketClient.observeNotification(observe));this.game = game;isObserver = true;} catch (Error e) {setTextColor("Red");out.print("\tCannot observe game.");}gameUI();}private void list() {if (!signedIn) {invalidCommand();return;}GameList resultGameList;try {GameData[] sortedList = getList();out.printf("\n\t%-14s | %-20s | %-20s | %-20s%n", "-- Game ID --", "-- White Player --", "-- Black Player --", "-- Game Name --");for (GameData gameData : sortedList) {int id = gameData.gameID();String white = gameData.whiteUsername();String black = gameData.blackUsername();String gameName = gameData.gameName();out.printf("\t%-14d | %-20s | %-20s | %-20s%n", id, white != null ? white : "null", black != null ? black : "null", gameName);}} catch (Error | IOException | InterruptedException e) {setTextStyle("Red");out.print("\tCannot list games,\n\t" + e.getMessage());}}private static GameData[] getList() throws IOException, InterruptedException {GameList resultGameList;resultGameList = ChessServerFacade.list(auth);HashSet<GameData> games = resultGameList.games();ArrayList<GameData> gamesList = new ArrayList<>(games);gamesList.sort(Comparator.comparingInt(GameData::gameID));GameData[] sortedList = gamesList.toArray(new GameData[0]);gameList = sortedList;return sortedList;}private void printBoard(String color, ChessBoard board) {ArrayList<ChessPosition> greenSquares = new ArrayList<>();for (ChessMove move : validMoves) {ChessPosition highLightPosition = move.getEndPosition();greenSquares.add(highLightPosition);}isPrinting = true;boolean isWhite = color.equals("WHITE");out.println();printTop(isWhite);boolean whiteSquare = true;for (int row = 0; row < 8; row++) {whiteSquare = row % 2 == 0;if (isWhite) row = 8 - row;else row = row + 1;setBackgroundColor("Blue");setTextColor("Black");out.print(" " + (row) + "\u2001");for (int col = 0; col < 8; col++) {if (isWhite) col = 7 - col;ChessPosition thisSquare = new ChessPosition(row, col + 1);if (whiteSquare) {setBackgroundColor("White");whiteSquare = false;if (greenSquares.contains(thisSquare)) setBackgroundColor("Green");} else {setBackgroundColor("Dark Grey");whiteSquare = true;if (greenSquares.contains(thisSquare)) setBackgroundColor("Dark Green");}ChessPiece currentPiece = board.getPiece(new ChessPosition(row, col + 1));if (currentPiece == null) {out.print(EMPTY);if (isWhite) col = 7 - col;continue;}ChessPiece.PieceType type = currentPiece.getPieceType();ChessGame.TeamColor teamColor = currentPiece.getTeamColor();char pieceChar = switch (type) {case KING -> 'K';case QUEEN -> 'Q';case BISHOP -> 'B';case KNIGHT -> 'N';case ROOK -> 'R';case PAWN -> 'P';};if (teamColor == ChessGame.TeamColor.WHITE) setTextColor("Blue");else setTextColor("Red");setTextStyle("Bold");out.print(" " + (pieceChar) + "\u2001");if (isWhite) col = 7 - col;}setBackgroundColor("Blue");setTextColor("Black");out.print(" " + (row) + "\u2001");setBackgroundColor("Black");out.print("\n");if (isWhite) row = 8 - row;else row = row - 1;}printTop(isWhite);setTextColor("White");out.print("\tIt is " + game.game().getTeamTurn().toString().toLowerCase() + "'s turn to move.");isPrinting = false;}private void printTop(boolean isWhite) {char[] alpha = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};setBackgroundColor("Blue");setTextColor("Black");out.print(EMPTY);for (int i = 0; i < 8; i++) {char outputChar = isWhite ? alpha[7 - i] : alpha[i];out.print(" " + outputChar + "\u2001");}out.print(EMPTY);setBackgroundColor("None");out.print("\n");}private void printPrompt() {out.print(ERASE_LINE);String status = signedIn ? "LOGGED_IN" : "LOGGED_OUT";if (isPlayer) status = "GAME";if (isObserver) status = "OBSERVING";setTextColor("White");out.print(RESET_TEXT_ITALIC);setTextStyle("Bold");String output = "\n" + "[" + status + "]" + EMPTY + ">>>" + EMPTY;out.print(output);}private void invalidCommand() {setTextColor("Red");out.print("\tInvalid Command!");setTextColor("Yellow");out.print("\n\tType \"help\" for available commands");}private void invalidArguments() {setTextColor("Red");out.print("\tInvalid arguments!");setTextColor("Yellow");out.print("\n\tType \"help\" for required arguments.");}private void setBackgroundColor(String color) {String bG = switch (color) {case "Black" -> SET_BG_COLOR_BLACK;case "White" -> SET_BG_COLOR_WHITE;case "Light Grey" -> SET_BG_COLOR_LIGHT_GREY;case "Dark Grey" -> SET_BG_COLOR_DARK_GREY;case "Blue" -> SET_BG_COLOR_BLUE;case "Green" -> SET_BG_COLOR_GREEN;case "Dark Green" -> SET_BG_COLOR_DARK_GREEN;case "Red" -> SET_BG_COLOR_RED;case "Yellow" -> SET_BG_COLOR_YELLOW;case "Magenta" -> SET_BG_COLOR_MAGENTA;case "None" -> RESET_BG_COLOR;default -> throw new IllegalStateException("Unexpected value: " + color);};out.print(bG);}private void setTextColor(String color) {String text = switch (color) {case "Black" -> SET_TEXT_COLOR_BLACK;case "White" -> SET_TEXT_COLOR_WHITE;case "Light Grey" -> SET_TEXT_COLOR_LIGHT_GREY;case "Dark Grey" -> SET_TEXT_COLOR_DARK_GREY;case "Blue" -> SET_TEXT_COLOR_BLUE;case "Green" -> SET_TEXT_COLOR_GREEN;case "Red" -> SET_TEXT_COLOR_RED;case "Yellow" -> SET_TEXT_COLOR_YELLOW;case "Magenta" -> SET_TEXT_COLOR_MAGENTA;default -> throw new IllegalStateException("Unexpected value: " + color);};out.print(text);}private void setTextStyle(String style) {String s = switch (style) {case "Bold" -> SET_TEXT_BOLD;case "Italic" -> SET_TEXT_ITALIC;case "Blinking" -> SET_TEXT_BLINKING;case "Faint" -> SET_TEXT_FAINT;case "Underline" -> SET_TEXT_UNDERLINE;default -> throw new IllegalStateException("Unexpected value: " + style);};out.print(RESET_TEXT_ITALIC);out.print(RESET_TEXT_BOLD_FAINT);out.print(RESET_TEXT_UNDERLINE);out.print(s);}public void ntfy(String message) {try {webSocketClient.send(message);} catch (Exception ignored) {System.out.println(ignored.getMessage());System.out.println("Error in ntfy() func");}}public void printNotification(Notification notice) {setTextColor("White");out.print("\nNotification:");setTextColor("Green");out.print("\n" + notice.getMessage());if (isPlayer && notice.getMessage().contains("resign")) {out.print(" You win!");}printPrompt();}public void printLoadGame(LoadGame load) {setTextColor("White");game = load.getGame();gameOver = load.getGameOver();redraw();printPrompt();}public void printError(ErrorNotification e) {setTextColor("Red");out.print("\nError:\n\t");out.print(e.getMessage());printPrompt();}}
