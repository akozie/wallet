package com.woleapp.netpos.qrgenerator.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.woleapp.netpos.qrgenerator.model.Row


class CardSchemeAdapter(
    categoryList: ArrayList<Row>,
    context: Context,
    layoutId:Int) :
    ArrayAdapter<Row>( context,layoutId,categoryList) {




    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = LayoutInflater.from(parent.context)
                 .inflate(android.R.layout.simple_expandable_list_item_1,
                  parent,
                false) as TextView

        view.text = super.getItem(position)?.card_scheme

        return view

    }

}


