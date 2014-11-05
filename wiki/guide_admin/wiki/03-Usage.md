<!-- --- title: Usage -->

<!-- SIMPLE HORIZONTAL NAVIGATOR -->
<div class="horizontal-navigator">
	<span>
		Previous: <a href="./02-Launching">Launching</a>
	</span>
</div>

## Content editing

There are 2 ways to edit the wiki content:

1. Using the wiki's edit feature. Changes will be automatically committed/pushed.
2. Using other system/console editors. To view the changes in your wiki, you have to:
	* commit changes (git) if the wiki is running on your machine,
	* push changes (git) if the wiki is running on a remote machine.

### Currently supported formats

Gollum supports many documentation/markup formats - see its GitHub page.  
This project's wiki is configured to only support:

1. AsciiDoc
2. Markdown
3. MediaWiki
4. Textile

This can be customized in Gollum configuration files. See the [[repository root folder|Repository-root-description]].

#### Syntax quick reference

1. AsciiDoc  
	<http://asciidoctor.org/docs/asciidoc-syntax-quick-reference/>
2. Markdown  
	<http://daringfireball.net/projects/markdown/syntax>
3. MediaWiki  
	<http://www.mediawiki.org/wiki/Help:Formatting/cs>
4. Textile  
	<http://redcloth.org/textile/writing-paragraph-text/>

#### Plugins for Eclipse

Maybe it's best to use a system defined editor, but for the sake of completeness, let's list relevant Eclipse plugins - some might even contain a WYSIWYG editor or live preview. Note, however, that none of them were tested or inspected in detail.

1. AsciiDoc, MediaWiki, Textile:  
	<http://eclipsewiki.sourceforge.net/>  
	<http://eclipse.dzone.com/articles/getting-started-wikitext>  
2. Markdown  
	<http://www.winterwell.com/software/markdown-editor.php>  
	<https://github.com/winterstein/Eclipse-Markdown-Editor-Plugin>

### Gollum's own gotchas

<https://github.com/gollum/gollum/wiki>

**Syntax highlighting**  
Do note, however, that the alleged GitHub file includes do not work as specified in the above link. Instead of:

<code>\`\`\`html:github:gollum/gollum/master/test/file_view/1_file.txt\`\`\`</code>

we have to use:

<code>\`\`\`java:/src/org/example/ExampleClass.java\`\`\`</code>

**Embedding video**  
Example:  
<pre><code>&lt;div class=&quot;video-container&quot;&gt;
	&lt;code class=&quot;vc-mime&quot;&gt;video/mp4&lt;/code&gt;
	&lt;a class=&quot;vc-source&quot;&gt;http://video-js.zencoder.com/oceans-clip.mp4&lt;/a&gt;
&lt;/div&gt;
</code></pre>

Current mime-type codec support for individual browsers:  
<http://www.videojs.com/html5-video-support/>