import java.io.Serializable;

public class Chunk implements Serializable {
    public final String textChunk;
    public final String pattern;

    public final int validStartOfChunk;
    public final int validStartEndOfChunk;

    public Chunk(String textChunk, String pattern, int validStartOfChunk, int validStartEndOfChunk)
    {
        this.textChunk = textChunk;
        this.pattern = pattern;

        this.validStartOfChunk = validStartOfChunk;
        this.validStartEndOfChunk = validStartEndOfChunk;
    }
}