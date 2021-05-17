<script lang="ts">
import { defineComponent, computed, Ref, ref, onMounted, watch, nextTick } from "vue";
import _ from "@/vendors/lodash";
import CodeMirror, { Editor } from "codemirror";
import "codemirror/lib/codemirror.css";
import "codemirror/mode/javascript/javascript.js";
import "codemirror/addon/fold/foldcode.js";
import "codemirror/addon/fold/brace-fold.js";
import "codemirror/addon/fold/foldgutter.js";
import "codemirror/addon/fold/foldgutter.css";
import "codemirror/addon/edit/matchbrackets.js";
import "codemirror/addon/search/searchcursor.js";
import CopyButton from "@/components/debug/CopyButton.vue";
import jwtDecode from "jwt-decode";

// eslint-disable-next-line
const makeRegExp = (queryStr: string, caseInsensitive=true) => new RegExp(queryStr.replace(/[\-\[\]\/\{\}\(\)\*\+\?\.\\\^\$\|]/g, "\\$&"), caseInsensitive ? "gi" : "g");

const SEARCH_OVERLAY_ID = "search-overlay"; // ends up in html as "cm-search-overlay" class

function makeSearchOverlay(queryStr: string, caseInsensitive=true): { name: string, token: any } {
	const query = makeRegExp(queryStr, caseInsensitive);

	return {
		name: SEARCH_OVERLAY_ID,
		token: function(stream) {
			query.lastIndex = stream.pos;
			const match = query.exec(stream.string);
			if (match && match.index == stream.pos) {
				stream.pos += match[0].length || 1;
				return SEARCH_OVERLAY_ID;
			} else if (match) {
				stream.pos = match.index;
			} else {
				stream.skipToEnd();
			}
		}
	};
}


type SearchHandler = {
	total: number,
	current: number,
	findNext: () => void,
	findPrev: () => void
}

const startSearch = (cm: Editor, query: string): Ref<SearchHandler> => {
	const regexp = makeRegExp(query);
	const match = cm.getValue().match(regexp);
	if (!match) {
		cm.setCursor(cm.getCursor());
		return ref({
			total: 0,
			current: 0,
			findNext: () => {},
			findPrev: () => {}
		});
	}

	const total = match.length;
	let cursor = cm.getSearchCursor(makeRegExp(query));

	const navigate = () => {
		cm.setSelection(cursor.from(), cursor.to());
		cm.scrollIntoView(null, 40);
	};

	const current = ref(1);
	cursor.findNext();
	navigate();

	const findFirst = () => {
		cursor = cm.getSearchCursor(makeRegExp(query));
		cursor.findNext();
		navigate();
		current.value = 1;
	};

	const findLast = () => {
		cursor = cm.getSearchCursor(makeRegExp(query), {
			line: cm.lastLine(),
			ch: cm.getLine(cm.lastLine()).length - 1
		});
		cursor.findPrevious();
		navigate();
		current.value = total;
	};

	const findNext = () => {
		if (current.value === total) {
			findFirst();
		} else {
			cursor.findNext();
			navigate();
			current.value += 1;
		}
	};

	const findPrev = () => {
		if (current.value === 1) {
			findLast();
		} else {
			cursor.findPrevious();
			navigate();
			current.value -= 1;
		}
	};

	return ref({
		total,
		current,
		findNext,
		findPrev
	});
};


export default defineComponent({
	components: { CopyButton },
	props: {
		data: {
			type: String,
			required: true
		},
		pretty: {
			type: Boolean,
			default: true
		},
		searchActive: {
			type: Boolean,
			default: false
		}
	},
	emits: ["update:search-active"],
	setup(props) {
		const rootDiv = ref<HTMLDivElement>();
		const prettyData = computed<string>(() => {
			try {
				return JSON.stringify(JSON.parse(props.data), null, "\t");
			} catch {
				return props.data;
			}
		});
		const finalData = computed<string>(() => props.pretty ? prettyData.value : props.data);
		let cmInstance: null | Editor = null;

		const idToken = computed(() => JSON.parse(props.data)?.id_token);

		onMounted(async () => {
			cmInstance = CodeMirror(rootDiv.value!, {
				mode: "application/json",
				readOnly: true,
				lineNumbers: true,
				gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
				foldOptions: {
					widget: " ... "
				},
				foldGutter: true,
				matchBrackets: true,
				viewportMargin: 20
			});

			watch(finalData, newData => {
				cmInstance!.setValue(newData);
				if (idToken.value) {
					addTokenOverlay(cmInstance);
				}
			}, { immediate: true });
		});

		const searchInputRef = ref<any>(null);
		watch(() => props.searchActive, async active => {
			if (active) {
				await nextTick();
				searchInputRef.value?.focus();
			}
		});

		const searchInputModel = ref<string>("");
		const searchValue = ref<string | null>("");
		watch(searchInputModel, () => searchValue.value = null);
		watch(searchInputModel, _.debounce(newValue => searchValue.value = newValue, 300));

		const searchHandler = ref<SearchHandler | null>(null);
		const updateSearchHandler = () => {
			if (!cmInstance) {
				return;
			}
			if (!searchValue.value) {
				searchHandler.value = null;
			} else {
				searchHandler.value = startSearch(cmInstance, searchValue.value).value;
			}
		};
		watch(finalData, () => {
			searchHandler.value = null;
			setTimeout(updateSearchHandler, 500); // wait till codemirror redraws with new content
		});
		watch(searchValue, updateSearchHandler);

		const activeSearchValue = computed<string | null>(() => props.searchActive ? searchValue.value : null);
		watch(activeSearchValue, newVal => {
			if (!cmInstance) {
				return;
			}
			cmInstance.removeOverlay(SEARCH_OVERLAY_ID);
			if (newVal) {
				cmInstance.addOverlay(makeSearchOverlay(newVal));
			}
		});

		const findNext = () => {
			searchHandler.value?.findNext();
		};

		const findPrev = () => {
			searchHandler.value?.findPrev();
		};

		const searchFieldEnter = (shiftKey: boolean) => {
			(shiftKey ? findPrev : findNext)();
		};

		const decodedIdToken = computed<string>(() => idToken.value ? JSON.stringify(jwtDecode(idToken.value), null, "\t") : "");
		const idTokenJsonViewer = ref<HTMLDivElement>();
		const idTokenInfo = ref<HTMLDivElement>();
		const showIdTokenPopover = ref<boolean>(false);

		function setTokenViewer() {
			const cmInstanceForToken = CodeMirror(idTokenJsonViewer.value!, {
				mode: "application/json",
				readOnly: true,
				gutters: ["CodeMirror-linenumbers", "CodeMirror-foldgutter"],
				foldOptions: {
					widget: " ... "
				},
				matchBrackets: true
			});

			cmInstanceForToken!.setValue(decodedIdToken?.value);
		}

		// find token_id and add info button for opening decoded token information
		// codemirror useful documentation
		// https://codemirror.net/doc/manual.html#addOverlay
		// https://codemirror.net/doc/manual.html#token
		function addTokenOverlay(cm) {
			if (!cm) {
				return;
			}

			const rx = /[,:]/; // Define what separates a word
			const tokenOverlay = {
				token(stream) {
					let ch = stream.peek();
					let word = "";

					if (rx.test(ch)) {
						stream.next();
						return null;
					}

					while ((ch = stream.peek()) && !rx.test(ch)) {
						word += ch;
						stream.next();
					}

					if (word.includes("id_token")) {
						return "id-token"; // CSS class: cm-id-token
					}
				}
			};

			// opaque will remove any spelling overlay etc
			cm!.addOverlay(tokenOverlay, { opaque: true });

			addTokenInfo();
		}

		// create info container and add it after token overlay
		// add inside token info component
		function addTokenInfo() {
			const tokenElement = document.querySelector(".cm-id-token + .cm-string");
			tokenElement!.before(idTokenInfo.value!);
		}

		watch(showIdTokenPopover, val => {
			if (val) {
				nextTick(() => setTokenViewer());
			}
		});

		return {
			rootDiv,
			searchInputModel,
			searchInputRef,
			searchHandler,
			findNext,
			findPrev,
			searchFieldEnter,
			idTokenJsonViewer,
			idTokenInfo,
			showIdTokenPopover,
			decodedIdToken
		};
	}
});
</script>

<template>
	<div
		ref="rootDiv"
		class="json-viewer"
	>
		<div
			v-if="searchActive"
			class="search-bar"
		>
			<div>
				<el-input
					ref="searchInputRef"
					v-model="searchInputModel"
					placeholder="Search..."
					class="input"
					@keydown.enter="searchFieldEnter($event.shiftKey)"
				>
					<template #prefix>
						<span class="icon-search"></span>
					</template>
				</el-input>

				<span v-if="searchHandler">
					<template v-if="searchHandler.total > 0">
						{{ searchHandler.current }} of {{ searchHandler.total }}
					</template>
					<template v-else>
						No results
					</template>
				</span>
			</div>

			<div>
				<button
					class="icon-button"
					@click.stop="findPrev"
				>
					<span class="icon-prev"></span>
				</button>
				<button
					class="icon-button"
					@click.stop="findNext"
				>
					<span class="icon-next"></span>
				</button>
				<button
					class="icon-button"
					@click="$emit('update:search-active', false)"
				>
					<span class="icon-close"></span>
				</button>
			</div>
		</div>
	</div>
	<span
		v-if="decodedIdToken"
		ref="idTokenInfo"
		class="id-token-info"
	>
		<el-popover
			v-model:visible="showIdTokenPopover"
			placement="left"
			:width="500"
			:height="200"
			popper-class="token-popover"
			trigger="click"
		>
			<template #reference>
				<button class="id-token-info-button"><span class="info-icon"></span></button>
			</template>
			<div class="popover-content">
				<div class="toolbar">
					<CopyButton
						:text="decodedIdToken"
					/>
					<button
						class="button-close"
						@click="showIdTokenPopover = false"
					>
						<span class="icon-close"></span>
					</button>
				</div>
				<div
					ref="idTokenJsonViewer"
					class="token-json-viewer"
				></div>
			</div>
		</el-popover>
	</span>
</template>

<style scoped lang="scss">
@import "~@/assets/scss/abstracts/variables";
@import "~@/assets/scss/abstracts/mixins";

$base-color: $global-text-color;
$line-padding: 5px;
$indent-guide-border: 1px solid $silver;
$indent-width: 20px;
$folder-icon-width: 8px;

$json-key-color: #00a398;
$json-string-color: #000f5b;
$json-number-color: #00b1f9;
$json-atom-color: #000f5b;

.json-viewer {
	height: 100%;
	width: 100%;
	position: relative;

	.search-bar {
		position: absolute;
		top: 3px;
		right: 8px;
		z-index: 1;
		display: flex;
		align-items: center;
		justify-content: space-between;
		background-color: $white-smoke-3;
		box-shadow: $global-box-shadow;
		padding: $global-margin-xsmall $global-margin-small;
		width: 390px;
		color: $grey;

		.input {
			width: 240px;
			margin-right: $global-margin-xsmall;
		}

		::v-deep(.el-input__prefix) {
			display: flex;
			align-items: center;
			left: 10px;
		}

		.icon-search {
			color: $grey;
			@include mask-icon("~@/assets/images/icon-search.svg", 11px);
		}

		.icon-prev {
			transform: rotate(180deg);
			@include mask-icon("~@/assets/images/icon-arrow-down.svg", 11px);
			mask-size: contain;
			mask-position: center;
		}

		.icon-next {
			@include mask-icon("~@/assets/images/icon-arrow-down.svg", 11px);
			mask-size: contain;
			mask-position: center;
		}

		.icon-close {
			@include mask-icon("~@/assets/images/close-icon.svg", 11px);
		}

		.icon-button {
			background: none;
			border: none;
			cursor: pointer;
			line-height: 0;
			padding: 2px;

			&:hover {
				color: $night-rider;
			}
		}
	}
}

.json-viewer,
.token-json-viewer {
	::v-deep(.CodeMirror) {
		height: 100%;
		color: $base-color;
		font-size: $global-font-size;
		font-weight: $global-font-weight-light;
		position: relative;
		z-index: 0;

		.CodeMirror-selected {
			background: none;

			&::after {
				display: block;
				position: absolute;
				top: $line-padding;
				bottom: $line-padding;
				left: 0;
				right: 0;
				background-color: $global-search-highlight-color;
				content: " ";
			}
		}

		.cm-overlay.cm-search-overlay {
			background-color: $global-search-highlight-color;
		}

		.CodeMirror-gutters {
			background-color: $white;
			border-right: $indent-guide-border;
		}

		.CodeMirror-lines {
			padding-top: 0;
		}

		.CodeMirror-line {
			padding-top: $line-padding;
			padding-bottom: $line-padding;
			padding-left: 1px;
		}

		.CodeMirror-linenumber {
			padding-top: $line-padding;
			padding-bottom: $line-padding;
			color: $base-color;
		}

		.CodeMirror-foldgutter {
			width: $folder-icon-width * 3;

			&-open,
			&-folded {
				padding: $line-padding 0 $line-padding $folder-icon-width;

				&::after {
					color: $base-color;
					content: "";
					@include mask-icon("~@/assets/images/arrow-right.svg", $folder-icon-width, $folder-icon-width);
					-webkit-mask-size: contain;
					-webkit-mask-position: center;
				}
			}

			&-open::after {
				transform: rotate(90deg);
			}
		}

		.CodeMirror-foldmarker {
			color: $base-color;
			text-shadow: none;
			font-family: inherit;
		}

		.cm-tab {
			width: $indent-width;
		}

		.cm-tab:not(:first-child) {
			position: relative;

			&::after {
				border-left: $indent-guide-border;
				position: absolute;
				top: -$line-padding;
				bottom: -$line-padding;
				left: 0;
				content: " ";
			}
		}

		.CodeMirror-matchingbracket {
			color: inherit;
			border: 1px solid $silver;
		}

		.cm-string {
			color: $json-string-color;
		}

		.cm-property {
			color: $json-key-color;
		}

		.cm-number {
			color: $json-number-color;
		}

		.cm-atom {
			color: $json-atom-color;
		}

		.cm-id-token {
			color: $json-key-color;
		}
	}
}

.id-token-info {
	position: relative;
}

.id-token-info-button {
	background: none;
	padding: 0;
	border: none;
	color: $global-muted-color;
	margin-right: 3px;

	.info-icon {
		vertical-align: text-bottom;
		@include mask-icon("~@/assets/images/icon-info.svg", 14px);
	}

	&:hover {
		cursor: pointer;
	}
}

.popover-content {
	height: 200px;
	display: flex;
	flex-direction: column;

	.toolbar {
		text-align: right;
		padding: $global-margin-small;

		.button-close {
			background: none;
			border: none;
			cursor: pointer;
			line-height: 0;
			padding: $global-margin-xsmall;
			margin: 0;
			color: $grey;

			.icon-close {
				@include mask-icon("~@/assets/images/icon-close.svg", 14px);
			}
		}
	}
}

.token-json-viewer {
	height: 100%;
	width: 100%;
	overflow: hidden;

	::v-deep(.CodeMirror) {
		.CodeMirror-gutter {
			width: 0;
		}

		.CodeMirror-gutters {
			border: none;
		}

		.CodeMirror-line {
			padding-left: $global-margin-medium;
		}

		.cm-tab:not(:first-child)::after {
			border: none;
		}
	}
}
</style>
