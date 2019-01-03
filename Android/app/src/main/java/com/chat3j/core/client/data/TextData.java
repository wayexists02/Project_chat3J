package com.chat3j.core.client.data;

import com.esotericsoftware.kryo.Kryo;

/**
 * 텍스트 데이터를 표현.
 * 노드들 사이 텍스트 메시지가 오간 후 이 데이터로 변환됨
 */
public class TextData extends Data<String> {

    public TextData() {

    }

    @Override
    public void register(Kryo kryo) {

    }
}
