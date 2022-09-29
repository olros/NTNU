package com.ntnu.gidd.security.extractor;

public interface TokenExtractor {
    String extract(String payload);
}
