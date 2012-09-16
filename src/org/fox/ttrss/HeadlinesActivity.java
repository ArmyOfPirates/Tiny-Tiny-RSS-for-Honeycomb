package org.fox.ttrss;

import java.util.ArrayList;

import org.fox.ttrss.types.Article;
import org.fox.ttrss.types.ArticleList;
import org.fox.ttrss.types.Feed;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class HeadlinesActivity extends OnlineActivity implements HeadlinesEventListener, ArticleEventListener {
private final String TAG = this.getClass().getSimpleName();
	
	protected SharedPreferences m_prefs;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		m_prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());

		if (m_prefs.getString("theme", "THEME_DARK").equals("THEME_DARK")) {
			setTheme(R.style.DarkTheme);
		} else {
			setTheme(R.style.LightTheme);
		}
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.headlines);
		
		if (!isCompatMode()) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		
		setSmallScreen(findViewById(R.id.headlines_fragment) == null); 
		
		if (savedInstanceState == null) {
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

			Intent i = getIntent();
			
			if (i.getExtras() != null) {
				Feed feed = i.getParcelableExtra("feed");
				Article activeArticle = i.getParcelableExtra("activeArticle");
				Article article = i.getParcelableExtra("article");
				
				ArrayList<Article> alist = i.getParcelableArrayListExtra("articles");
				ArticleList articles  = new ArticleList();
				
				for (Article a : alist)
					articles.add(a);
				
				HeadlinesFragment hf = new HeadlinesFragment(feed, activeArticle, articles);

				ft.replace(R.id.headlines_fragment, hf, FRAG_HEADLINES);
				
				ArticlePager af = new ArticlePager(article, hf.getAllArticles());
				
				ft.replace(R.id.article_fragment, af, FRAG_ARTICLE);
			}
			
			ft.commit();
		} 
	}
	
	@Override
	protected void loginSuccess() {
		Log.d(TAG, "loginSuccess");
		
		setLoadingStatus(R.string.blank, false);
		findViewById(R.id.loading_container).setVisibility(View.GONE);
		
		initMenu();
	}
	
	@Override
	public void onSaveInstanceState(Bundle out) {
		super.onSaveInstanceState(out);		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		default:
			Log.d(TAG, "onOptionsItemSelected, unhandled id=" + item.getItemId());
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public boolean getUnreadArticlesOnly() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	protected void initMenu() {
		super.initMenu();

		if (m_menu != null && m_sessionId != null) {
			m_menu.setGroupVisible(R.id.menu_group_feeds, false);

			HeadlinesFragment hf = (HeadlinesFragment)getSupportFragmentManager().findFragmentByTag(FRAG_HEADLINES);
			
			m_menu.setGroupVisible(R.id.menu_group_headlines, hf != null && hf.getSelectedArticles().size() == 0);
			m_menu.setGroupVisible(R.id.menu_group_headlines_selection, hf != null && hf.getSelectedArticles().size() != 0);
			
			Fragment af = getSupportFragmentManager().findFragmentByTag(FRAG_ARTICLE);
			
			m_menu.setGroupVisible(R.id.menu_group_article, af != null);
		}		
	}
	
	@Override
	public void onArticleListSelectionChange(ArticleList m_selectedArticles) {
		initMenu();
	}

	@Override
	public void onArticleSelected(Article article) {
		onArticleSelected(article, true);
	}

	@Override
	public void onArticleSelected(Article article, boolean open) {
		
		if (open) {
			FragmentTransaction ft = getSupportFragmentManager()
					.beginTransaction();
			
			HeadlinesFragment hf = (HeadlinesFragment) getSupportFragmentManager().findFragmentByTag(FRAG_HEADLINES);
			
			Fragment frag = new ArticlePager(article, hf.getAllArticles());

			ft.replace(R.id.article_fragment, frag, FRAG_ARTICLE);
			//ft.addToBackStack(null);
			
			ft.commit();
		} else {
			HeadlinesFragment hf = (HeadlinesFragment) getSupportFragmentManager().findFragmentByTag(FRAG_HEADLINES);
			if (hf != null) hf.setActiveArticle(article);
		}
		
	}
}