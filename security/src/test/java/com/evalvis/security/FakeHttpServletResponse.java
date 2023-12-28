package com.evalvis.security;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

public class FakeHttpServletResponse implements HttpServletResponse {
    @Override
    public void addCookie(Cookie cookie) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean containsHeader(String name) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String encodeURL(String url) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String encodeRedirectURL(String url) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void sendError(int sc, String msg) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void sendError(int sc) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void sendRedirect(String location) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setDateHeader(String name, long date) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addDateHeader(String name, long date) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setHeader(String name, String value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addHeader(String name, String value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setIntHeader(String name, int value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void addIntHeader(String name, int value) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setStatus(int sc) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int getStatus() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getHeader(String name) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Collection<String> getHeaders(String name) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Collection<String> getHeaderNames() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getCharacterEncoding() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public String getContentType() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public ServletOutputStream getOutputStream() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public PrintWriter getWriter() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setCharacterEncoding(String charset) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setContentLength(int len) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setContentLengthLong(long length) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setContentType(String type) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setBufferSize(int size) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public int getBufferSize() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void flushBuffer() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void resetBuffer() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public boolean isCommitted() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void setLocale(Locale loc) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Locale getLocale() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
