package reference_data;

public class Get {

    @Override
    public String get(Board board) {
        this.board = board;
        this.graph=new Node[board.size()][board.size()];

        for (int x=1;x<board.size();x++){
            for (int y = 1; y < board.size(); y++) {
                if(board.isAt(x,y,Elements.NONE,Elements.GOOD_APPLE)){
                    graph[x][y]=new Node(new PointImpl(x,y));
                    if(board.isAt(x-1,y,Elements.NONE,Elements.GOOD_APPLE)){
                        if(graph[x-1][y]==null){
                            graph[x-1][y]=new Node(new PointImpl(x-1,y));
                            graph[x-1][y].neighbors.add(graph[x][y]);
                            graph[x][y].neighbors.add(graph[x-1][y]);
                        }

                        if (graph[x + 1][y] == null) {
                            graph[x + 1][y] = new Node(new PointImpl(x + 1, y));
                            graph[x + 1][y].neighbors.add(graph[x][y]);
                            graph[x][y].neighbors.add(graph[x + 1][y]);
                        }

                        if (graph[x][y-1] == null) {
                            graph[x][y-1] = new Node(new PointImpl(x, y-1));
                            graph[x ][y-1].neighbors.add(graph[x][y]);
                            graph[x][y].neighbors.add(graph[x ][y-1]);
                        }
                        if (graph[x][y+1] == null) {
                            graph[x][y+1] = new Node(new PointImpl(x, y+1));
                            graph[x ][y+1].neighbors.add(graph[x][y]);
                            graph[x][y].neighbors.add(graph[x ][y+1]);
                        }
                    }
                }
            }
        }

}
