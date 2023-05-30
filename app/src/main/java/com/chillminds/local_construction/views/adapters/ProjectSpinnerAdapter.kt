package com.chillminds.local_construction.views.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.chillminds.local_construction.R
import com.chillminds.local_construction.repositories.remote.dto.ProjectDetail


class ProjectSpinnerAdapter(context: Context, projectList: List<ProjectDetail>?) :
    ArrayAdapter<ProjectDetail>(context, 0, projectList ?: arrayListOf()) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        var localView: View? = convertView
        if (localView == null) {
            localView =
                LayoutInflater.from(context).inflate(R.layout.project_spinner_view, parent, false)
        }
        localView?.findViewById<TextView>(R.id.spinnerTextView)?.text = getItem(position)?.name?:""

        return localView!!
    }

}