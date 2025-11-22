package apinexo.common.configuration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

public class CachedBodyHttpServletResponse extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream cachedContent = new ByteArrayOutputStream();
    private ServletOutputStream outputStream;
    private PrintWriter writer;

    public CachedBodyHttpServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {

        if (this.outputStream == null) {
            ServletOutputStream original = super.getOutputStream();
            this.outputStream = new ServletOutputStream() {
                @Override
                public void write(int b) throws IOException {
                    original.write(b);
                    cachedContent.write(b);
                }

                @Override
                public boolean isReady() {
                    return original.isReady();
                }

                @Override
                public void setWriteListener(WriteListener writeListener) {
                    original.setWriteListener(writeListener);
                }
            };
        }

        return this.outputStream;
    }

    @Override
    public PrintWriter getWriter() throws IOException {

        if (this.writer == null) {
            this.writer = new PrintWriter(new OutputStreamWriter(cachedContent, getCharacterEncoding()), true);
        }

        return this.writer;
    }

    public byte[] getCachedBody() {
        return cachedContent.toByteArray();
    }
}
