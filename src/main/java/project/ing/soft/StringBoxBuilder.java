package project.ing.soft;

import java.util.Arrays;
import java.util.stream.IntStream;


public class StringBoxBuilder {
    private final BoxCharset c;
    private final int width;
    private final int height;
    private int lineFilled;
    private StringBuilder topBuilder;
    private StringBuilder bottomBuilder;


    public StringBoxBuilder(BoxCharset c, int width, int height) {
        this.c = c;
        this.width = width;
        this.height = height;

        this.topBuilder = new StringBuilder();
        this.bottomBuilder = new StringBuilder();
        this.lineFilled = 2;

        this.topBuilder.append(buildTopLine()).append('\n');
        this.bottomBuilder.append(buildBottomLine() ).append('\n');
    }

    private static String padding(int paddingWidth){
        if(paddingWidth == 0)
            return "";
        return String.format(String.format("%%%ds", paddingWidth),"");
    }

    private String buildHorizontalLine(int length){
        return new String(new char[length]).replace('\0', c.horizontal());
    }

    private String buildTopLine(){
        return c.topLeft() + buildHorizontalLine(width-2)+ c.topRight();
    }

    private String buildBottomLine(){
        return c.bottomLeft() + buildHorizontalLine(width-2)+ c.bottomRight();
    }

    private String buildMiddleLine(){
        return c.middleLeft() + buildHorizontalLine(width-2)+ c.middleRight();
    }

    private String buildVoidContentLines()
    {
        return c.vertical() + new String(new char[width-2]).replace('\0', ' ' )+ c.vertical();
    }

    private String buildContentLines(String content) {
        if(content.length() > width -2 ){

            int wrappingPoint = wrap(content, width-2);
            String firstChars ;
            String lastChars;

            if(wrappingPoint == -1) {
                firstChars = content.substring(0, width - 2) + "-";
                lastChars = content.substring(width-2);
            }else {
                firstChars = content.substring(0, content.charAt(wrappingPoint) == ' ' ? wrappingPoint : wrappingPoint+1);
                lastChars = content.substring(wrappingPoint+1 ) ;
            }


            return buildContentLines( firstChars)+"\n"+ buildContentLines( lastChars);
        }

        int leftPadding = (width - 2 - content.length() ) /2;
        int rightPadding = width -2 - content.length() -leftPadding;
        lineFilled++;

        return c.vertical() + padding(leftPadding) + content + padding(rightPadding) + c.vertical();
    }



    public void appendToTop(String content){

        topBuilder.append(buildContentLines(content.replace("\n", ""))).append('\n');
        lineFilled++;
    }

    public void appendInAboxToTop(String content){
        appendToTop(content);
        topBuilder.append(buildMiddleLine()).append('\n');

    }


    public void prependToBottom(String content){
        bottomBuilder.insert(0,buildContentLines(content.replace("\n", ""))+"\n");
        lineFilled++;
    }

    public void prependInAboxToBottom(String content){
        prependToBottom(content);

        bottomBuilder.insert(0,buildMiddleLine()+"\n");

    }

    @Override
    public String toString(){
        for (int i = 0; i < height - lineFilled; i++) {
            topBuilder.append(buildVoidContentLines()).append("\n");
        }
        return topBuilder.toString()+bottomBuilder.toString();
    }

    //These function is intended to find the best place where slitting the string
    //for wrapping purpose. If a string is spliced where a space occurs the space could be deleted from
    //the string as the carriage return will be enough expressive.
    // the function returns the index, intended as inclusive, where the string should we splitted
    private int wrap(String content, int wrappingLimit) {
        char[] punctuation =  ".,?! ".toCharArray();
        Arrays.sort(punctuation);

        if(content.charAt(wrappingLimit) ==' ')
            return wrappingLimit;
        for (int i = wrappingLimit-1; i >= 0; i--) {
            if(Arrays.binarySearch(punctuation, content.charAt(i)) >= 0)
                return i;
        }

        return -1;
    }

    public static String drawNear( int first, int last, Object... others ){
        if(first == last) {
            return others[first].toString();
        }
        else {
            int tmp = (first + last ) /2;
            String one = drawNear(first, tmp, others);
            String two = drawNear(tmp +1, last, others );

            String[] ones = one.split("\\n");
            String paddingOne = padding(ones[0].length());
            String[] twos = two.split("\\n");
            String paddingTwo = padding(twos[0].length());
            StringBuilder sb = IntStream
                    .range(0, Math.max(ones.length, twos.length))
                    .mapToObj(i ->
                         (i < ones.length ? ones[i] : paddingOne) +
                         (i < twos.length ? twos[i] : paddingTwo) +
                                "\n"
                    )
                    .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append );
            return sb.toString();

        }

    }
    public static String drawNear( Object... others ){
        return drawNear(0, others.length-1, others);
    }



    public interface BoxCharset{
        char horizontal();
        char vertical();
        char topLeft();
        char topRight();
        char bottomLeft();
        char bottomRight();
        char middleLeft();
        char middleRight();
    }

    public static class SINGLELINEROUNDEDCORNER implements BoxCharset{
        //SINGLE LINE ROUNDED CORNER
        private static final char SINGLE_LINE_ROUNDED_HORIZONTAL    = '─';
        private static final char SINGLE_LINE_ROUNDED_VERTICAL      = '│';
        private static final char SINGLE_LINE_ROUNDED_TOP_LEFT      = '╭';
        private static final char SINGLE_LINE_ROUNDED_TOP_RIGHT     = '╮';
        private static final char SINGLE_LINE_ROUNDED_BOTTOM_LEFT   = '╰';
        private static final char SINGLE_LINE_ROUNDED_BOTTOM_RIGHT  = '╯';
        private static final char SINGLE_LINE_ROUNDED_MIDDLE_LEFT   = '├';
        private static final char SINGLE_LINE_ROUNDED_MIDDLE_RIGHT  = '┤';

        @Override
        public char horizontal() {
            return SINGLE_LINE_ROUNDED_HORIZONTAL;
        }

        @Override
        public char vertical() {
            return SINGLE_LINE_ROUNDED_VERTICAL;
        }

        @Override
        public char topLeft() {
            return SINGLE_LINE_ROUNDED_TOP_LEFT;
        }

        @Override
        public char topRight() {
            return SINGLE_LINE_ROUNDED_TOP_RIGHT;
        }

        @Override
        public char bottomLeft() {
            return SINGLE_LINE_ROUNDED_BOTTOM_LEFT;
        }

        @Override
        public char bottomRight() {
            return SINGLE_LINE_ROUNDED_BOTTOM_RIGHT;
        }

        @Override
        public char middleLeft() {
            return SINGLE_LINE_ROUNDED_MIDDLE_LEFT;
        }

        @Override
        public char middleRight() {
            return SINGLE_LINE_ROUNDED_MIDDLE_RIGHT;
        }
    }

    public static class DOUBLELINESQUAREANGLE  implements BoxCharset{
        //DOUBLE LINE SQUARED CORNER
        private static final char DOUBLE_LINE_SQUARED_HORIZONTAL    = '═';
        private static final char DOUBLE_LINE_SQUARED_VERTICAL      = '║';
        private static final char DOUBLE_LINE_SQUARED_TOP_LEFT      = '╔';
        private static final char DOUBLE_LINE_SQUARED_TOP_RIGHT     = '╗';
        private static final char DOUBLE_LINE_SQUARED_BOTTOM_LEFT   = '╚';
        private static final char DOUBLE_LINE_SQUARED_BOTTOM_RIGHT  = '╝';
        private static final char DOUBLE_LINE_SQUARED_MIDDLE_LEFT   = '╠';
        private static final char DOUBLE_LINE_SQUARED_MIDDLE_RIGHT  = '╣';

        @Override
        public char horizontal() {
            return DOUBLE_LINE_SQUARED_HORIZONTAL;
        }

        @Override
        public char vertical() {
            return DOUBLE_LINE_SQUARED_VERTICAL;
        }

        @Override
        public char topLeft() {
            return DOUBLE_LINE_SQUARED_TOP_LEFT;
        }

        @Override
        public char topRight() {
            return DOUBLE_LINE_SQUARED_TOP_RIGHT;
        }

        @Override
        public char bottomLeft() {
            return DOUBLE_LINE_SQUARED_BOTTOM_LEFT;
        }

        @Override
        public char bottomRight() {
            return DOUBLE_LINE_SQUARED_BOTTOM_RIGHT;
        }

        @Override
        public char middleLeft() {
            return DOUBLE_LINE_SQUARED_MIDDLE_LEFT;
        }

        @Override
        public char middleRight() {
            return DOUBLE_LINE_SQUARED_MIDDLE_RIGHT;
        }
    }



}
