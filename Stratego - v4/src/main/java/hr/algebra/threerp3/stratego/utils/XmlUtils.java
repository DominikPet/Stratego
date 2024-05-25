package hr.algebra.threerp3.stratego.utils;

import hr.algebra.threerp3.stratego.HelloController;
import hr.algebra.threerp3.stratego.model.Colors;
import hr.algebra.threerp3.stratego.model.Figure;
import hr.algebra.threerp3.stratego.model.GameMove;
import hr.algebra.threerp3.stratego.model.Position;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static hr.algebra.threerp3.stratego.utils.XmlCreateUtils.createDocument;
import static hr.algebra.threerp3.stratego.utils.XmlCreateUtils.createElement;

public class XmlUtils implements Serializable {

    public static final String FILENAME = "xml/gameMoves.xml";
    public static final String BOARDFILENAME = "xml/board.xml";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    public static void saveGameMove(GameMove gameMove) {

        List<GameMove> gameMoveList = new ArrayList<>();
        if (ButtonLogicUtils.firstGameMoveSet) gameMoveList = getAllGameMoves();
        gameMoveList.add(gameMove);

        try {
            Document document = createDocument("gameMoves");
            for (GameMove gm : gameMoveList) {
                Element newGameMove = document.createElement("gameMove");
                document.getDocumentElement().appendChild(newGameMove);

                newGameMove.appendChild(createElement(document, "startPosition.row", gm.getStartPositionRow()));
                newGameMove.appendChild(createElement(document, "startPosition.col", gm.getStartPositionCol()));
                newGameMove.appendChild(createElement(document, "endPosition.row", gm.getEndPositionRow()));
                newGameMove.appendChild(createElement(document, "endPosition.col", gm.getEndPositionCol()));
                newGameMove.appendChild(createElement(document, "dateTime",
                        gm.getTime().format(formatter)));
            }
            saveDocument(document, FILENAME);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void saveBoard(Figure[][] figures) {

        try {
            Document document = createDocument("board");

            for (int i = 0; i < HelloController.NUM_OF_ROWS; i++) {
                for (int j = 0; j < HelloController.NUM_OF_COLS; j++) {
                    Element gameBoardPosition = document.createElement("gameBoardPosition");
                    document.getDocumentElement().appendChild(gameBoardPosition);

                    gameBoardPosition.appendChild(createElement(document, "row", Integer.toString(i)));
                    gameBoardPosition.appendChild(createElement(document, "col", Integer.toString(j)));
                    gameBoardPosition.appendChild(createElement(document, "figure", figures[j][i].role));
                    gameBoardPosition.appendChild(createElement(document, "color", figures[j][i].color.toString()));
                }
            }
            saveDocument(document, BOARDFILENAME);
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    private static void saveDocument(Document document, String fileName) throws TransformerException {
        File file = new File(fileName);
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(new DOMSource(document), new StreamResult(new File(fileName)));
    }

    public static List<GameMove> getAllGameMoves() {

        List<GameMove> gameMoveList = new ArrayList<>();
        if (!ButtonLogicUtils.firstGameMoveSet) return gameMoveList;
        if (new File(FILENAME).exists()) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new File(FILENAME));
                Element gameMovesDocumentElement = document.getDocumentElement();
                NodeList gameMovesNodeList = gameMovesDocumentElement.getChildNodes();
                Position startPosition = new Position(0, 0);
                Position endPosition = new Position(0, 0);
                LocalDateTime time = LocalDateTime.now();

                for (int i = 0; i < gameMovesNodeList.getLength(); i++) {
                    Node gameMoveNode = gameMovesNodeList.item(i);

                    if (gameMoveNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element gameMoveElement = (Element) gameMoveNode;

                        NodeList gameMoveElementNodeList = gameMoveElement.getChildNodes();

                        for (int j = 0; j < gameMoveElementNodeList.getLength(); j++) {

                            if (gameMoveElementNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                Element gameMoveChildElement = (Element) gameMoveElementNodeList.item(j);

                                switch (gameMoveChildElement.getTagName()) {
                                    case "startPosition.row" ->
                                            startPosition.row = (Integer.parseInt(gameMoveChildElement.getTextContent()));
                                    case "startPosition.col" ->
                                            startPosition.col = (Integer.parseInt(gameMoveChildElement.getTextContent()));
                                    case "endPosition.row" ->
                                            endPosition.row = (Integer.parseInt(gameMoveChildElement.getTextContent()));
                                    case "endPosition.col" ->
                                            endPosition.col = (Integer.parseInt(gameMoveChildElement.getTextContent()));
                                    case "dateTime" -> time = LocalDateTime.parse(
                                            gameMoveChildElement.getTextContent(), formatter);
                                }
                            }
                        }
                        GameMove newGameMove = new GameMove(startPosition.row, startPosition.col, endPosition.row, endPosition.col, time);
                        gameMoveList.add(newGameMove);
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                ex.printStackTrace();
            }
        }
        return gameMoveList;
    }

    public static Figure[][] getBoard() {
        Figure[][] figures = new Figure[HelloController.NUM_OF_ROWS][HelloController.NUM_OF_COLS];
        if (new File(BOARDFILENAME).exists()) {

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document document = builder.parse(new File(BOARDFILENAME));
                Element boardDocumentElement = document.getDocumentElement();

                NodeList gameMovesNodeList = boardDocumentElement.getChildNodes();

                int row = 0;
                int col = 0;
                Colors color;

                for (int i = 0; i < gameMovesNodeList.getLength(); i++) {
                    Node boardNode = gameMovesNodeList.item(i);

                    if (boardNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element gameMoveElement = (Element) boardNode;

                        NodeList gameMoveElementNodeList = gameMoveElement.getChildNodes();

                        for (int j = 0; j < gameMoveElementNodeList.getLength(); j++) {

                            if (gameMoveElementNodeList.item(j).getNodeType() == Node.ELEMENT_NODE) {
                                Element gameMoveChildElement = (Element) gameMoveElementNodeList.item(j);

                                switch (gameMoveChildElement.getTagName()) {
                                    case "row" -> col = (Integer.parseInt(gameMoveChildElement.getTextContent()));
                                    case "col" -> row = (Integer.parseInt(gameMoveChildElement.getTextContent()));
                                    case "figure" -> {
                                        figures[row][col] = new Figure();
                                        figures[row][col].role = gameMoveChildElement.getTextContent();
                                    }
                                    case "color" ->
                                            figures[row][col].color = Colors.valueOf(gameMoveChildElement.getTextContent());
                                }
                                if (figures[row][col] != null)
                                    figures[row][col].rank = BoardUtils.getRank(figures[row][col].role);
                            }
                        }
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                ex.printStackTrace();
            }
        }
        return figures;
    }
}
