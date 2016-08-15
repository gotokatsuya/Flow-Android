package com.yuyakaido.android.flow.domain.usecase

import com.yuyakaido.android.flow.domain.entity.Article
import com.yuyakaido.android.flow.domain.entity.Category
import com.yuyakaido.android.flow.domain.entity.HatenaCategory
import com.yuyakaido.android.flow.domain.entity.Site
import com.yuyakaido.android.flow.infra.repository.HatenaRepository
import com.yuyakaido.android.flow.infra.repository.MenthasRepository
import com.yuyakaido.android.flow.infra.repository.QiitaRepository
import rx.Observable

/**
 * Created by yuyakaido on 7/30/16.
 */
class GetArticleUseCase(
        private val menthasRepository: MenthasRepository,
        private val qiitaRepository: QiitaRepository,
        private val hatenaRepository: HatenaRepository) {

    fun getArticleFetcher(site: Site, category: Category): () -> Observable<List<Article>> {
        return when (site) {
            Site.Menthas -> getMenthasArticleFetcher(category)
            Site.Qiita -> getQiitaArticleFetcher()
            Site.Hatena -> getHatenaArticleFetcher()
            else -> {
                fun () = Observable.empty<List<Article>>()
            }
        }
    }

    fun getQiitaArticleFetcher(): () -> Observable<List<Article>> {
        var isFetching = false
        var page = 1
        return fun () = Observable.just(isFetching)
                .filter { !isFetching }
                .doOnNext { isFetching = true }
                .flatMap { qiitaRepository.getArticles(page).toObservable() }
                .doOnNext { isFetching = false }
                .doOnNext { page++ }
    }

    fun getMenthasArticleFetcher(category: Category): () -> Observable<List<Article>> {
        var isFetching = false
        var offset = 0
        return fun () = Observable.just(isFetching)
                .filter { !isFetching }
                .doOnNext { isFetching = true }
                .flatMap { menthasRepository.getArticles(category, offset).toObservable() }
                .doOnNext { isFetching = false }
                .doOnNext { offset += it.size }
    }

    fun getHatenaArticleFetcher(): () -> Observable<List<Article>> {
        var isFirstFetch = true
        return fun () = Observable.just(isFirstFetch)
                .filter { isFirstFetch }
                .doOnNext { isFirstFetch = false }
                .flatMap { hatenaRepository.getArticles(HatenaCategory("it.rss")).toObservable() }
    }

}