package com.yuyakaido.android.flow.presentation.fragment

import android.net.Uri
import android.os.Bundle
import android.support.customtabs.CustomTabsIntent
import android.support.v4.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.yuyakaido.android.flow.R
import com.yuyakaido.android.flow.domain.entity.Article
import com.yuyakaido.android.flow.domain.entity.Category
import com.yuyakaido.android.flow.domain.entity.Site
import com.yuyakaido.android.flow.presentation.adapter.ArticleListAdapter
import com.yuyakaido.android.flow.presentation.adapter.ItemClickListener
import com.yuyakaido.android.flow.presentation.item.QiitaSubscription
import com.yuyakaido.android.flow.presentation.presenter.ArticleListPresenter
import java.io.Serializable

/**
 * Created by yuyakaido on 6/20/16.
 */
class ArticleListFragment : BaseFragment(), ItemClickListener<Article> {

    companion object {
        private val ARGS_COMPONENT = "ARGS_COMPONENT"

        fun newMenthasInstance(site: Site, category: Category): Fragment {
            return newInstance(Component(site, category, null))
        }

        fun newQiitaInstance(qiitaSubscription: QiitaSubscription): Fragment {
            return newInstance(Component(Site.Qiita, null, qiitaSubscription))
        }

        fun newInstance(component: Component): Fragment {
            val args = Bundle()
            args.putSerializable(ARGS_COMPONENT, component)
            val fragment = ArticleListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    class Component(
            val site: Site,
            val category: Category?,
            val qiitaSubscription: QiitaSubscription?) : Serializable

    private val component by lazy { arguments.getSerializable(ARGS_COMPONENT) as Component }

    private lateinit var presenter: ArticleListPresenter
    private lateinit var adapter: ArticleListAdapter

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_article_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        presenter = ArticleListPresenter(this, component)
        presenter.onCreate()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

    override fun onItemClick(item: Article) {
        startWebBrowser(item)
    }

    fun initialize() {
        adapter = ArticleListAdapter(context, mutableListOf(), this)

        val swipeRefreshLayout = view?.findViewById(R.id.fragment_article_list_swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener { presenter.refresh() }

        val layoutManager = LinearLayoutManager(context)
        val recyclerView = view?.findViewById(R.id.fragment_article_list_recycler_view) as RecyclerView
        recyclerView.adapter = adapter
        recyclerView.layoutManager = layoutManager

        presenter.registerPaginationTrigger(recyclerView, layoutManager)
    }

    fun clearArticles() {
        adapter.clear()
        adapter.notifyDataSetChanged()
    }

    fun addArticles(articles: List<Article>) {
        adapter.addAll(articles)
        adapter.notifyDataSetChanged()
    }

    fun startWebBrowser(article: Article) {
        val intent = CustomTabsIntent.Builder().addDefaultShareMenuItem().build()
        intent.launchUrl(activity, Uri.parse(article.url()))
    }

    fun showProgressBar() {
        val swipeRefreshLayout = view?.findViewById(R.id.fragment_article_list_swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout.isRefreshing = true
    }

    fun hideProgressBar() {
        val swipeRefreshLayout = view?.findViewById(R.id.fragment_article_list_swipe_refresh_layout) as SwipeRefreshLayout
        swipeRefreshLayout.isRefreshing = false
    }

}