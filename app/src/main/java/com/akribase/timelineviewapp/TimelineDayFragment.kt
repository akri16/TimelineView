package com.akribase.timelineviewapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.akribase.timelineviewapp.databinding.TimelineDayFragmentBinding


class TimelineDayFragment : Fragment() {

    companion object {
        private const val BUNDLE_POSITION_KEY = "POSITION"
        fun instantiate(pos: Int) = TimelineDayFragment().apply {
            arguments = Bundle().apply { putInt(BUNDLE_POSITION_KEY, pos) }
        }
    }

    private lateinit var binding: TimelineDayFragmentBinding
    private var position = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt(BUNDLE_POSITION_KEY) ?: 0
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = TimelineDayFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.timelineView.timelineEvents = ArrayList<com.akribase.timelineview.Event>().apply {
            add(com.akribase.timelineview.Event("Orgy", 1636949888, 1636959000))
            add(com.akribase.timelineview.Event("Party", 1636960100, 1636966000))
            add(com.akribase.timelineview.Event("Free Schwag", 1636967000, 1636987000))
        }

    }
}