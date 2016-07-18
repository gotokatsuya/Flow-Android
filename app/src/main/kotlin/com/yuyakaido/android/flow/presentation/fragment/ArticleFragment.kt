package com.yuyakaido.android.flow.presentation.fragment

import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.yuyakaido.android.flow.R
import com.yuyakaido.android.flow.domain.MenthasArticle
import com.yuyakaido.android.flow.domain.MenthasCategory
import com.yuyakaido.android.flow.infra.repository.MenthasRepository
import com.yuyakaido.android.flow.presentation.adapter.ArticleListAdapter
import com.yuyakaido.android.flow.util.ErrorHandler
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by yuyakaido on 6/20/16.
 */
class ArticleFragment : BaseFragment() {

    private var category: MenthasCategory? = null
    private var adapter: ArticleListAdapter? = null

    companion object {
        val ARGS_CATEGORY = "ARGS_CATEGORY"

        fun newInstance(menthasCategory: MenthasCategory) : Fragment {
            val args = Bundle()
            args.putSerializable(ARGS_CATEGORY, menthasCategory)
            val fragment = ArticleFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_article, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        category = arguments.getSerializable(ARGS_CATEGORY) as MenthasCategory
        adapter = ArticleListAdapter(context, mutableListOf())

        val listView = view?.findViewById(R.id.fragment_article_list_view) as ListView
        listView.adapter = adapter
        listView.setOnItemClickListener {
            adapterView, view, i, l -> startWebBrowser(adapter!!.getItem(i))
        }

        fetchArticles()
    }

    private fun fetchArticles() {
        MenthasRepository.getArticles(category!!)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { initListView(it) },
                        { ErrorHandler.handle(it) })
    }

    private fun initListView(articles: List<MenthasArticle>) {
        adapter?.addAll(articles)
        adapter?.notifyDataSetChanged()
    }

    private fun startWebBrowser(article: MenthasArticle) {
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(activity, Uri.parse(article.url))
    }

}