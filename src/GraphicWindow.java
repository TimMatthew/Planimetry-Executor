import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GraphicWindow extends JPanel {

    private static final int STEP_SIZE = 20;
    private static final int VALUE_STEP = 10;
    private static final int WINDOW_SIZE = 800;

    private List<CommandNode> syntaxTree;
    List<CommandNode> idTable = new ArrayList<>();

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawCartesianGrid(g);
        if (syntaxTree != null) {
            executeCommands(g);
        }
    }

    public void setSyntaxTree(List<CommandNode> syntaxTree) {
        this.syntaxTree = syntaxTree;
    }

    public void setIdTable(List<CommandNode> idTable) {
        this.idTable = idTable;
    }

    private void drawPoint(Graphics g, int x, int y, String id) {
        int coordX = getWidth() / 2 + x * STEP_SIZE;
        int coordY = getHeight() / 2 - y * STEP_SIZE;
        g.setColor(Color.BLACK);
        g.fillOval(coordX - 5, coordY - 5, 10, 10);   
        g.setFont(new Font("Arial Black", Font.PLAIN, 14));
        g.drawString(id, coordX + 5, coordY - 5);   
    }

    private void drawCircle(Graphics g, int x, int y, int radius, MarkPointNode node){

        // Case: коло вказане з центром і координатами
        for(CommandNode n : idTable){
            if(n instanceof MarkPointNode){
                if(!((MarkPointNode) n).getPointID().equals(node.getId())){
                    drawPoint(g, node.getX(), node.getY(), node.getId());
                }
            }
        }


        int coordX = getWidth() / 2 + x * STEP_SIZE;
        int coordY = getHeight() / 2 - y * STEP_SIZE;
        int coordRadius = radius * STEP_SIZE * 2;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2.0f));
        g.drawOval(coordX-coordRadius/2, coordY-coordRadius/2, coordRadius, coordRadius);
    }

    private void drawChord(Graphics g, int startX, int startY, int endX, int endY){

        int coordStartX = getWidth() / 2 + startX * STEP_SIZE;
        int coordStartY = getHeight() / 2 - startY * STEP_SIZE;
        int coordEndX = getWidth() / 2 + endX * STEP_SIZE;
        int coordEndY =  getHeight() / 2 - endY * STEP_SIZE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2.0f));
        g.setColor(Color.BLUE);
        g.drawLine(coordStartX, coordStartY, coordEndX, coordEndY);
    }

    private void drawSegment(Graphics g, int startX, int startY, int endX, int endY) {
        int coordStartX = getWidth() / 2 + startX * STEP_SIZE;
        int coordStartY = getHeight() / 2 - startY * STEP_SIZE;
        int coordEndX = getWidth() / 2 + endX * STEP_SIZE;
        int coordEndY =  getHeight() / 2 - endY * STEP_SIZE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2.0f));
        g.setColor(Color.BLUE);
        g.drawLine(coordStartX, coordStartY, coordEndX, coordEndY);
    }


    private void drawRadius(Graphics g, int centerX, int centerY, int endX, int endY){

        int coordStartX = getWidth() / 2 + centerX * STEP_SIZE;
        int coordStartY = getHeight() / 2 - centerY * STEP_SIZE;
        int coordEndX = getWidth() / 2 + endX * STEP_SIZE;
        int coordEndY =  getHeight() / 2 - endY * STEP_SIZE;

        Graphics2D g2d = (Graphics2D) g;
        g2d.setStroke(new BasicStroke(2.0f));
        g.setColor(Color.BLUE);
        g.drawLine(coordStartX, coordStartY, coordEndX, coordEndY);
    }

    private void drawCartesianGrid(Graphics g) {
        int width = getWidth();
        int height = getHeight();

        g.setColor(Color.LIGHT_GRAY);
        for (int i = STEP_SIZE; i < width; i += STEP_SIZE) {
            g.drawLine(i, 0, i, height);
        }
        for (int i = STEP_SIZE; i < height; i += STEP_SIZE) {
            g.drawLine(0, i, width, i);
        }

        g.setColor(Color.BLACK);
        g.drawLine(0, height / 2, width, height / 2);
        g.drawLine(width / 2, 0, width / 2, height);

        g.setColor(Color.RED);
        for (int i = 0; i < width / 2; i += 5 * STEP_SIZE) {
            int value = (i / (5 * STEP_SIZE)) * VALUE_STEP / 2;
            g.drawString(String.valueOf(value), width / 2 + i, height / 2 + 15);
            g.drawString(String.valueOf(-value), width / 2 - i, height / 2 + 15);
        }

        for (int i = 100; i < height / 2; i += 5 * STEP_SIZE) {
            int value = (i / (5 * STEP_SIZE)) * VALUE_STEP / 2;
            g.drawString(String.valueOf(-value), width / 2 + 5, height / 2 + i);
            g.drawString(String.valueOf(value), width / 2 + 5, height / 2 - i);
        }
    }

    public void executeCommands(Graphics g) {
        for (CommandNode node : syntaxTree) {

            if (node instanceof MarkPointNode markPoint) {
                drawPoint(g, markPoint.getX(), markPoint.getY(), markPoint.getPointID());
            } else if (node instanceof DrawCircleNode circleNode) {
                if (circleNode.getCenterNode() == null) {
                    for (CommandNode id : idTable) {
                        if (id instanceof MarkPointNode) {
                            String identifier = id.getId();
                            if (circleNode.getId().equals(identifier)) {
                                circleNode.setCenterNode((MarkPointNode) id);
                                circleNode.setCenterX(((MarkPointNode) id).getX());
                                circleNode.setCenterY(((MarkPointNode) id).getY());
                                drawCircle(g, circleNode.getCenterNode().getX(), circleNode.getCenterNode().getY(), circleNode.getRadius(), circleNode.getCenterNode());
                            }
                        }
                    }
                } else {
                    drawCircle(g, circleNode.getCenterNode().getX(), circleNode.getCenterNode().getY(), circleNode.getRadius(), circleNode.getCenterNode());
                }
            } else if (node instanceof DrawChordNode chordNode) {
                for (CommandNode id : idTable) {
                    if (id instanceof MarkPointNode) {
                        String identifier = id.getId();
                        if (chordNode.getStart().equals(identifier)) {
                            chordNode.setStartNode((MarkPointNode) id);
                        } else if (chordNode.getEnd().equals(identifier)) {
                            chordNode.setEndNode((MarkPointNode) id);
                        }

                        if (chordNode.getStartNode() != null && chordNode.getEndNode() != null) {
                            drawChord(g, chordNode.getStartNode().getX(), chordNode.getStartNode().getY(), chordNode.getEndNode().getX(), chordNode.getEndNode().getY());
                        }
                    }
                }
            } else if (node instanceof DrawSegmentNode segmentNode) {

                MarkPointNode startNode = null;
                MarkPointNode endNode = null;

                for (CommandNode id : idTable) {
                    if (id instanceof MarkPointNode) {
                        String identifier = id.getId();

                        if (segmentNode.getStart().equals(identifier)) {
                            startNode = (MarkPointNode) id;
                        } else if (segmentNode.getEnd().equals(identifier)) {
                            endNode = (MarkPointNode) id;
                        }
                    } else if (id instanceof DrawCircleNode circleNode) {
                        String circleId = circleNode.getId();

                          
                        if (segmentNode.getStart().equals(circleId)) {
                            startNode = circleNode.getCenterNode();
                        } else if (segmentNode.getEnd().equals(circleId)) {
                            endNode = circleNode.getCenterNode();
                        }
                    }

                      
                    if (startNode != null && endNode != null) {
                        drawSegment(g, startNode.getX(), startNode.getY(), endNode.getX(), endNode.getY());
                    }
                }
            } else if (node instanceof DrawRadiusNode radiusNode) {
                String radiusCenterID = radiusNode.getCenter();
                String radiusEndID = radiusNode.getEnd();

                for (CommandNode commandNode : idTable) {
                    if (commandNode instanceof MarkPointNode && ((MarkPointNode) commandNode).getPointID().equals(radiusCenterID)) {
                        radiusNode.setCenterNode((MarkPointNode) commandNode);
                    } else if (commandNode instanceof MarkPointNode && ((MarkPointNode) commandNode).getPointID().equals(radiusEndID)) {
                        radiusNode.setEndNode((MarkPointNode) commandNode);
                    }

                    if (radiusNode.getCenterNode() != null && radiusNode.getEndNode() != null) {
                        drawRadius(g, radiusNode.getCenterNode().getX(), radiusNode.getCenterNode().getY(), radiusNode.getEndNode().getX(), radiusNode.getEndNode().getY());
                    }
                }
            }
        }
    }


    public static GraphicWindow createAndShowGUI() {
        JFrame frame = new JFrame("Cartesian Coordinate System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GraphicWindow panel = new GraphicWindow();
        panel.setPreferredSize(new Dimension(WINDOW_SIZE + 400, WINDOW_SIZE));
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GraphicWindow::createAndShowGUI);
    }
}
