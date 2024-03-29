package com.ando.views.spinner;

public interface OnItemEditListener {
        /**
         * 点击监听
         * @param text          被点击的文本
         * @param isSelected    是否被选择
         */
        void onClick(String text, boolean isSelected);
    }