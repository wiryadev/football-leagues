package com.wiryatech.footballleagues.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.wiryatech.footballleagues.R
import com.wiryatech.footballleagues.adapters.MatchAdapter
import com.wiryatech.footballleagues.api.ApiRepository
import com.wiryatech.footballleagues.matches.MatchListPresenter
import com.wiryatech.footballleagues.matches.MatchListView
import com.wiryatech.footballleagues.models.Match
import com.wiryatech.footballleagues.ui.activities.MatchActivity
import com.wiryatech.footballleagues.utils.invisible
import com.wiryatech.footballleagues.utils.visible
import kotlinx.android.synthetic.main.fragment_next_match.*
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.toast

class NextMatchFragment : Fragment(), MatchListView {

    private var matches: MutableList<Match> = mutableListOf()
    private lateinit var presenter: MatchListPresenter
    private lateinit var matchAdapter: MatchAdapter
    private lateinit var idLeague: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_next_match, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        matchAdapter = MatchAdapter(matches) {
            context?.toast(it.idEvent)
            startActivity<MatchActivity>(MatchActivity.EXTRA_EVENT to it.idEvent)
        }

        val request = ApiRepository()
        val gson = Gson()
        presenter = MatchListPresenter(this, request, gson)

        initUI()

        Log.d("BundleFragment1", "$savedInstanceState, $arguments")
        if (arguments != null) {
            idLeague = arguments?.getString(PrevMatchFragment.ID).toString()
            Log.d("Next", "$arguments, $idLeague")
            presenter.getNextMatch(idLeague)
        }

        swipeRefresh.setOnRefreshListener {
            presenter.getNextMatch(idLeague)
        }
    }

    private fun initUI() {
        rv_next_match.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = matchAdapter
        }
    }

    override fun showLoading() {
        progressBarNext.visible()
    }

    override fun hideLoading() {
        progressBarNext.invisible()
        swipeRefresh.isRefreshing = false
    }

    override fun showMatchList(data: List<Match>) {
        iv_error.invisible()
        tv_error.invisible()
        swipeRefresh.isRefreshing = false
        matches.clear()
        matches.addAll(data)
        matchAdapter.notifyDataSetChanged()
    }

    override fun showNoData() {
        swipeRefresh.isRefreshing = false
        iv_error.setImageResource(R.drawable.no_signal)
        tv_error.text = getString(R.string.no_data)
        iv_error.visible()
        tv_error.visible()
    }

    override fun showNoConnection() {
        swipeRefresh.isRefreshing = false
        iv_error.setImageResource(R.drawable.no_signal)
        tv_error.text = getString(R.string.no_connection)
        iv_error.visible()
        tv_error.visible()
    }

}