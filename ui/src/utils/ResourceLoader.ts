import axios from "axios";

export default class ResourceLoader {
	_searches: any;
	_batchSize: number;
	_currentSearchIdx: number;
	_nextUrl: string | null;
	loading: boolean;
	results: any;
	meta: any;

	constructor(searches, batchSize=20) {
		this._searches = searches;
		this._batchSize = batchSize;
		this._currentSearchIdx = 0;
		this.loading = false;
		this.results = Object.fromEntries(Object.keys(searches).map(key => [key, []]));
		this.meta = Object.fromEntries(Object.keys(searches).map(key => [key, {}]));
	}

	get finished() {
		return this._currentSearchIdx >= Object.keys(this._searches).length;
	}

	get _currentSearchId() {
		return Object.keys(this._searches)[this._currentSearchIdx];
	}

	load() {
		if (this.loading) {
			return;
		}

		if (this.finished) {
			return;
		}

		this._loadNextBatch();
	}

	_loadNextBatch() {
		if (this._nextUrl) {
			this._handle(axios.get(this._nextUrl));
		} else {
			this._handle(
				axios.get(this._searches[this._currentSearchId], { params: { _count: this._batchSize } })
			);
		}
	}

	_handle(request) {
		this.loading = true;
		request.then(({ data }) => {
			const newEntries = data.entry ? data.entry.map(e => e.resource) : [];
			this.results[this._currentSearchId].push(...newEntries);
			this.meta[this._currentSearchId] = { total: data.total, id: data.id, meta: data.meta };
			const nextLink = data.link.find(l => l.relation === "next");
			if (nextLink) {
				this._nextUrl = nextLink.url.replace(/\*/, "");
			} else {
				this._nextUrl = null;
				this._currentSearchIdx += 1;
			}

			if (newEntries.length < this._batchSize && !this.finished) {
				this._loadNextBatch();
			} else {
				this.loading = false;
			}
		}).catch(() => {
			this.loading = false;
		});
	}
}
